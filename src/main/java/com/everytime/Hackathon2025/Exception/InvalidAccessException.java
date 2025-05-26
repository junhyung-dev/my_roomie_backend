package com.everytime.Hackathon2025.Exception;

//내 설문조사가 아닌데 접근
public class InvalidAccessException extends RuntimeException {
    public InvalidAccessException() {
        super("잘못된 접근입니다.");
    }
}
