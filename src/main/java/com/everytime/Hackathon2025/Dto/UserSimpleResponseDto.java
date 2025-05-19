package com.everytime.Hackathon2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSimpleResponseDto {
    private Long id;
    private String username;
    private String name;
}