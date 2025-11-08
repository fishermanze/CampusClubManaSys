// 活动审批结果消费者
package com.club.message.consumer;

import com.club.common.dto.MessageDTO;
import com.club.message.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class ActivityResultConsumer {
    @Resource
    private MessageService messageService;

    @RabbitListener(queues = "activity.result.queue")
    public void handleActivityResult(MessageDTO message) {
        messageService.saveMessage(message.getReceiverId(), message.getContent(), message.getType());
        System.out.println("处理活动审批结果通知：" + message.getContent());
    }
}