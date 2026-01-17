package com.churchsoft.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private List<String> allowedOrigins = new ArrayList<>();
}