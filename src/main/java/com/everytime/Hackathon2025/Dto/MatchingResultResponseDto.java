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
    private String etc;
    private String createdAt;
    private double matchingRate; //일치율 정보
}
