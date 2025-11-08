package com.club.message.consumer;

import com.club.message.entity.Message;
import com.club.message.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import com.club.common.dto.MessageDTO;

@Component
public class ApplyNoticeConsumer {

    @Resource
    private MessageService messageService;

    // 消费社团申请通知（创始人接收）
    @RabbitListener(queues = "club.apply.queue")
    public void handleApplyNotice(Message message) {
        // 保存消息到数据库（实际业务可扩展：WebSocket推送、短信通知等）
        messageService.saveMessage(
                message.getReceiverId(),
                message.getContent(),
                message.getType()
        );
        System.out.println("处理申请通知：" + message.getContent());
    }
}