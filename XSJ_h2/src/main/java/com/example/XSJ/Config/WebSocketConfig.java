package com.example.XSJ.Config;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 扩大 WebSocket 消息大小限制（解决图片/大Base64发送失败）
     * 默认只有 8KB → 改成 10MB
     */
    @Bean
    public WebSocketContainer webSocketContainer() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        container.setDefaultMaxTextMessageBufferSize(10 * 1024 * 1024);
        container.setDefaultMaxBinaryMessageBufferSize(10 * 1024 * 1024);

        return container;
    }
}