// 活动申请通知消费者
package com.club.message.consumer;

import com.club.common.dto.MessageDTO;
import com.club.message.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class ActivityApplyConsumer {
    @Resource
    private MessageService messageService;

    @RabbitListener(queues = "activity.apply.queue")
    public void handleActivityApply(MessageDTO message) {
        messageService.saveMessage(message.getReceiverId(), message.getContent(), message.getType());
        System.out.println("处理活动申请通知：" + message.getContent());
    }
}