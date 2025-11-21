package com.shemuel.timeline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "utools")
public class UtoolsProperties {
    private String pluginId;
    private String secret;
}
