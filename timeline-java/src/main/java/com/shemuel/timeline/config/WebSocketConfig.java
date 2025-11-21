package com.shemuel.timeline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    /**
     * 启用 JSR-356 标准的 @ServerEndpoint 支持
     * 仅在使用 Spring Boot 内嵌容器时需要；如果你是部署到外部 Tomcat，就不要配这个 Bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
