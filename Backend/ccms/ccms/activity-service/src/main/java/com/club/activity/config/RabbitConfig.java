package com.club.activity.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列配置类（活动服务专用）
 * 定义活动申请、审批结果相关队列，确保消息可靠传输
 */
@Configuration
public class RabbitConfig {

    /**
     * 活动申请通知队列（发给社团创始人）
     * 用于：活动申请提交后，通知创始人处理
     */
    public static final String ACTIVITY_APPLY_QUEUE = "activity.apply.queue";

    /**
     * 活动审批结果队列（发给申请人）
     * 用途：创始人处理申请后，通知申请人结果
     */
    public static final String ACTIVITY_RESULT_QUEUE = "activity.result.queue";

    /**
     * 声明活动申请队列
     * 特性：持久化（队列重启不丢失）、非排他（多消费者可共享）、非自动删除
     */
    @Bean
    public Queue activityApplyQueue() {
        return QueueBuilder.durable(ACTIVITY_APPLY_QUEUE)
                .exclusive() // 无参数调用，默认false（允许跨连接共享队列）
                .autoDelete() // 队列无消费者时不自动删除
                .withArgument("x-message-ttl", 86400000) // 消息过期时间：24小时（避免死消息堆积）
                .build();
    }

    /**
     * 声明活动审批结果队列
     * 特性：同申请队列，确保结果通知可靠送达
     */
    @Bean
    public Queue activityResultQueue() {
        return QueueBuilder.durable(ACTIVITY_RESULT_QUEUE)
                .exclusive()
                .autoDelete()
                .withArgument("x-message-ttl", 86400000) // 消息24小时过期
                .build();
    }
}