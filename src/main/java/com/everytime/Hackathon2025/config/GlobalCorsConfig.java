package com.everytime.Hackathon2025.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 프론트엔드 개발 주소
                .allowedOrigins(
                        "http://127.0.0.1:5500",
                        "http://127.0.0.1:5500/index.html",
                        "http://localhost:5500",
                        // 필요하다면 ngrok가 새로 발급한 주소도 추가
                        "https://minnow-perfect-humbly.ngrok-free.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}