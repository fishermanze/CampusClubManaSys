package com.campusclub.message.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单的消息代理，用于将消息广播到客户端
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 设置应用程序目的地前缀
        registry.setApplicationDestinationPrefixes("/app");
        
        // 设置用户目的地前缀
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端将使用此端点连接到服务器
        registry.addEndpoint("/ws-message")
                .setAllowedOrigins("*") // 允许所有来源的连接
                .withSockJS(); // 启用SockJS协议作为备选
    }
}