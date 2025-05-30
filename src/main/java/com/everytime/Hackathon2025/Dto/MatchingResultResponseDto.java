package com.everytime.Hackathon2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingResultResponseDto {
    private long id;
    private UserSimpleResponseDto user;
    private String dormName;
    private String cleanLevel;
    private boolean smoking;
    private boolean snoring;
    private String sleepTime;
    private String wakeUpTime;
    private String etc;
    private String createdAt;
    private double matchingRate; //일치율 정보
    private double matchingScore; //선호도 기반 매칭점수
}
