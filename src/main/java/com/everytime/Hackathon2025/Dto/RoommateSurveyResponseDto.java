package com.everytime.Hackathon2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoommateSurveyResponseDto {
    private long id;
    private UserSimpleResponseDto user;
    private String dormName;
    private String cleanLevel;
    private boolean smoking;
    private String etc;
    private String createdAt;
    private boolean isMine;
    private String sleepTime;
    private String wakeUpTime;
    private String phoneTime;
    private String showerTime;
}
