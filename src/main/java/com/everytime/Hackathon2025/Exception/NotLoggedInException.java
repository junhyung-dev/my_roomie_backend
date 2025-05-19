package com.everytime.Hackathon2025.Exception;

/** 세션(로그인) 정보가 없을 때 */
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super("로그인되지 않았습니다.");
    }
}