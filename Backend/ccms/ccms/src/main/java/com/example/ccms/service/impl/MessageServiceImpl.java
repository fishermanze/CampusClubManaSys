package com.example.ccms.service.impl;

import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.MessageVO;
import com.example.ccms.entity.Message;
import com.example.ccms.enums.ErrorCodeEnum;
import com.example.ccms.enums.MessageTypeEnum;
import com.example.ccms.exception.BusinessException;
import com.example.ccms.repository.MessageRepository;
import com.example.ccms.repository.UsersRepository;
import com.example.ccms.service.MessageService;
import com.example.ccms.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UsersRepository usersRepository;

    @Override
    public ApiResponse<List<MessageVO>> getUserMessages(Long userId, int pageNum, int pageSize) {
        if (!usersRepository.existsById(userId)) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Message> messagePage = messageRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        List<MessageVO> messageVOs = messagePage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return ResultUtil.success(messageVOs);
    }

    @Override
    public ApiResponse<Long> getUnreadCount(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        Long unreadCount = messageRepository.countByUserIdAndIsReadFalse(userId);
        return ResultUtil.success(unreadCount);
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MESSAGE_NOT_FOUND));
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR);
        }
        message.setIsRead(true);
        messageRepository.save(message);
        return ResultUtil.success();
    }

    @Override
    @Transactional
    public ApiResponse<Void> markAllAsRead(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        messageRepository.markAllByUserIdAndIsReadFalseAsRead(userId);
        return ResultUtil.success();
    }

    @Override
    @Transactional
    public void sendMessage(Long userId, Long clubId, String content, MessageTypeEnum type) {
        if (!usersRepository.existsById(userId)) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        Message message = new Message();
        message.setUserId(userId);
        message.setClubId(clubId);
        message.setContent(content);
        message.setType(type);
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        messageRepository.save(message);
    }

    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setContent(message.getContent());
        vo.setType(message.getType());
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime()); // 调用MessageVO的setter自动格式化
        vo.setClubId(message.getClubId());
        return vo;
    }
}