package com.club.message.consumer;

import com.club.common.dto.MessageDTO; // 替换之前的本地DTO引用
import com.club.message.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 消费审批结果通知（发给申请人）
 */
@Component
public class ApplyResultConsumer {

    @Resource
    private MessageService messageService;

    @RabbitListener(queues = "apply.result.queue") // 监听审批结果队列
    public void handleApplyResult(MessageDTO message) {
        // 保存消息到数据库
        messageService.saveMessage(
                message.getReceiverId(),
                message.getContent(),
                message.getType()
        );
        // 可扩展：WebSocket实时推送、短信通知等
        System.out.println("处理审批结果通知：" + message.getContent());
    }
}