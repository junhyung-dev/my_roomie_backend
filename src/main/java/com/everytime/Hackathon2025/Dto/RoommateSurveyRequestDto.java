package com.everytime.Hackathon2025.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoommateSurveyRequestDto {

    private String dormName;
    private String cleanLevel;
    private boolean smoking;
    private boolean snoring;

    private String sleepTime;
    private String wakeUpTime;

    private String etc;

    private int dormImportance = 3;
    private int cleanImportance = 3;
    private int sleepTimeImportance = 3;
    private int wakeUpTimeImportance = 3;

    private int smokingPreference = 2;

    private int snoringPreference = 2;
}
