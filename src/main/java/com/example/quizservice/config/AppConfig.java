package com.example.quizservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Used for calling the FastAPI proctoring service via REST
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
