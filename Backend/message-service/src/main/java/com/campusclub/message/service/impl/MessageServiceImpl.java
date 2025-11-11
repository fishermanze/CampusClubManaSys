package com.campusclub.message.service.impl;

import com.campusclub.message.dto.MessageDTO;
import com.campusclub.message.dto.ConversationDTO;
import com.campusclub.message.entity.Message;
import com.campusclub.message.entity.MessageReadRecord;
import com.campusclub.message.exception.BusinessException;
import com.campusclub.message.repository.MessageRepository;
import com.campusclub.message.repository.MessageReadRecordRepository;
import com.campusclub.message.service.MessageService;
import com.campusclub.message.service.UserServiceClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageReadRecordRepository messageReadRecordRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public MessageDTO sendMessage(Message message) {
        // 保存消息
        Message savedMessage = messageRepository.save(message);
        
        // 更新消息阅读记录
        updateMessageReadRecord(savedMessage);
        
        // 转换为DTO
        MessageDTO messageDTO = modelMapper.map(savedMessage, MessageDTO.class);
        
        // 填充发送者信息
        try {
            messageDTO.setSenderName(userServiceClient.getUserNameById(savedMessage.getSenderId()));
            messageDTO.setSenderAvatar(userServiceClient.getUserAvatarById(savedMessage.getSenderId()));
        } catch (Exception e) {
            // 如果用户服务调用失败，使用默认值
            messageDTO.setSenderName("用户" + savedMessage.getSenderId());
            messageDTO.setSenderAvatar("/default-avatar.png");
        }
        
        return messageDTO;
    }

    @Override
    public List<MessageDTO> getConversationMessages(Long userId1, Long userId2, int page, int size) {
        List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
                userId1, userId2, userId2, userId1);
        
        // 转换为DTO
        return messages.stream().map(message -> {
            MessageDTO dto = modelMapper.map(message, MessageDTO.class);
            try {
                dto.setSenderName(userServiceClient.getUserNameById(message.getSenderId()));
                dto.setSenderAvatar(userServiceClient.getUserAvatarById(message.getSenderId()));
            } catch (Exception e) {
                dto.setSenderName("用户" + message.getSenderId());
                dto.setSenderAvatar("/default-avatar.png");
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ConversationDTO> getUserConversations(Long userId) {
        List<MessageReadRecord> records = messageReadRecordRepository.findByUserIdOrderByLastReadTimeDesc(userId);
        
        return records.stream().map(record -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setUserId(record.getOtherUserId());
            dto.setUnreadCount(record.getUnreadCount());
            dto.setIsSticky(record.getIsSticky());
            dto.setIsMuted(record.getIsMuted());
            dto.setLastReadTime(record.getLastReadTime());
            
            // 填充对方用户信息
            try {
                dto.setUserName(userServiceClient.getUserNameById(record.getOtherUserId()));
                dto.setUserAvatar(userServiceClient.getUserAvatarById(record.getOtherUserId()));
            } catch (Exception e) {
                dto.setUserName("用户" + record.getOtherUserId());
                dto.setUserAvatar("/default-avatar.png");
            }
            
            // 获取最新消息
            Message latestMessage = messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
                    userId, record.getOtherUserId(), record.getOtherUserId(), userId).stream().findFirst().orElse(null);
            
            if (latestMessage != null) {
                MessageDTO messageDTO = modelMapper.map(latestMessage, MessageDTO.class);
                try {
                    messageDTO.setSenderName(userServiceClient.getUserNameById(latestMessage.getSenderId()));
                    messageDTO.setSenderAvatar(userServiceClient.getUserAvatarById(latestMessage.getSenderId()));
                } catch (Exception e) {
                    messageDTO.setSenderName("用户" + latestMessage.getSenderId());
                    messageDTO.setSenderAvatar("/default-avatar.png");
                }
                dto.setLastMessage(messageDTO);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long receiverId, Long senderId) {
        // 标记消息为已读
        messageRepository.markMessagesAsRead(receiverId, senderId);
        
        // 重置未读计数
        Optional<MessageReadRecord> recordOpt = messageReadRecordRepository.findByUserIdAndOtherUserId(receiverId, senderId);
        if (recordOpt.isPresent()) {
            MessageReadRecord record = recordOpt.get();
            Message latestMessage = messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
                    senderId, receiverId, receiverId, senderId).stream().findFirst().orElse(null);
            
            if (latestMessage != null) {
                messageReadRecordRepository.resetUnreadCount(receiverId, senderId, LocalDateTime.now(), latestMessage.getId());
            }
        }
    }

    @Override
    public long getUnreadMessageCount(Long userId) {
        Long count = messageReadRecordRepository.getTotalUnreadCount(userId);
        return count != null ? count : 0;
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("消息不存在"));
        
        // 验证权限（只有发送者或接收者可以删除消息）
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权删除此消息");
        }
        
        messageRepository.softDeleteMessage(messageId, userId);
    }

    @Override
    @Transactional
    public void updateConversationStickyStatus(Long userId, Long otherUserId, Boolean isSticky) {
        messageReadRecordRepository.updateStickyStatus(userId, otherUserId, isSticky);
    }

    @Override
    @Transactional
    public void updateConversationMuteStatus(Long userId, Long otherUserId, Boolean isMuted) {
        messageReadRecordRepository.updateMuteStatus(userId, otherUserId, isMuted);
    }

    /**
     * 更新消息阅读记录
     */
    private void updateMessageReadRecord(Message message) {
        // 为接收者更新阅读记录
        Optional<MessageReadRecord> recordOpt = messageReadRecordRepository.findByUserIdAndOtherUserId(
                message.getReceiverId(), message.getSenderId());
        
        if (recordOpt.isPresent()) {
            // 增加未读计数
            messageReadRecordRepository.incrementUnreadCount(message.getReceiverId(), message.getSenderId());
        } else {
            // 创建新的阅读记录
            MessageReadRecord newRecord = new MessageReadRecord();
            newRecord.setUserId(message.getReceiverId());
            newRecord.setOtherUserId(message.getSenderId());
            newRecord.setUnreadCount(1);
            newRecord.setLastReadTime(message.getCreatedAt());
            messageReadRecordRepository.save(newRecord);
        }
    }
}