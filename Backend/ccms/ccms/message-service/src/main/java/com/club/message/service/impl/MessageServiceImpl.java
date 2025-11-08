package com.club.message.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.message.entity.Message;
import com.club.message.mapper.MessageMapper;
import com.club.message.service.MessageService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public void saveMessage(Long receiverId, String content, Integer type) {
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type);
        message.setReadStatus(0); // 未读
        message.setCreateTime(LocalDateTime.now());
        save(message);
    }

    @Override
    public IPage<Message> getUserMessages(Page<Message> page, Long receiverId) {
        return baseMapper.selectByReceiverId(page, receiverId);
    }

    @Override
    public void markAsRead(List<Long> messageIds) {
        messageIds.forEach(id -> {
            Message message = new Message();
            message.setId(id);
            message.setReadStatus(1); // 已读
            updateById(message);
        });
    }
}