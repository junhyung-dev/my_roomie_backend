//package com.everytime.Hackathon2025.config;
//
//import org.springframework.context.annotation.*;
//import org.springframework.web.servlet.config.annotation.*;
//
//@Configuration
//public class cors implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")                                  // 모든 엔드포인트에 적용
//                .allowedOrigins("https://efa5-220-65-129-125.ngrok-free.app")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)                            // 쿠키 기반 인증 허용 시
//                .maxAge(3600);                                     // preflight 캐시 시간 (초)
//    }
//}
