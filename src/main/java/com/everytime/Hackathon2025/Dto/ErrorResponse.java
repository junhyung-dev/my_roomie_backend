package com.everytime.Hackathon2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 모든 오류 응답의 공통 포맷 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;     // 예: USER_NOT_FOUND, INVALID_PASSWORD
    private final String message;  // 예: 존재하지 않는 회원입니다.
}