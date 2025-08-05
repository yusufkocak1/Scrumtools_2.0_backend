package com.kocak.scrumtoolsbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Memory-optimized simple broker configuration
        config.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{25000, 25000}) // Heartbeat optimization
                .setTaskScheduler(null); // Use default scheduler to save memory

        config.setApplicationDestinationPrefixes("/app");

        // Message size limits to prevent memory issues
        config.setCacheLimit(1024);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000) // Optimize heartbeat
                .setDisconnectDelay(5000) // Faster cleanup of disconnected clients
                .setStreamBytesLimit(512 * 1024) // 512KB limit
                .setHttpMessageCacheSize(1000); // Limit cache size
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // Memory and performance optimization for WebSocket transport
        registration.setMessageSizeLimit(64 * 1024) // 64KB message limit
                .setSendBufferSizeLimit(512 * 1024) // 512KB send buffer
                .setSendTimeLimit(20000) // 20 second send timeout
                .setTimeToFirstMessage(60000); // First message timeout
    }
}
