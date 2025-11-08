package com.club.message.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 社团申请通知队列（发给创始人）
    public static final String CLUB_APPLY_QUEUE = "club.apply.queue";
    // 审批结果通知队列（发给申请人）
    public static final String APPLY_RESULT_QUEUE = "apply.result.queue";

    @Bean
    public Queue clubApplyQueue() {
        return new Queue(CLUB_APPLY_QUEUE, true); // 持久化队列
    }

    @Bean
    public Queue applyResultQueue() {
        return new Queue(APPLY_RESULT_QUEUE, true);
    }
}