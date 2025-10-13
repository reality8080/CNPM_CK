package hcmute.edu.vn.web.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker cho pub/sub (dùng in-memory, không cần RabbitMQ cho dev)
        config.enableSimpleBroker("/topic");  // Prefix cho topic (client subscribe /topic/users)
        config.setApplicationDestinationPrefixes("/app");  // Prefix cho message từ client (ví dụ: /app/send-user-update)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint cho client connect (SockJS hỗ trợ fallback)
        registry.addEndpoint("/ws")  // URL: ws://localhost:8080/ws
                .setAllowedOriginPatterns("*")  // Cho phép CORS từ tất cả origins (thay bằng cụ thể cho prod)
                .withSockJS();  // Enable SockJS
    }

}
