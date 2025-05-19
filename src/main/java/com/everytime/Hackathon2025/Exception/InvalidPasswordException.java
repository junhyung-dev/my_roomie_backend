package com.everytime.Hackathon2025.Exception;

/** 비밀번호가 틀렸을 때 발생 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("비밀번호가 잘못됐습니다.");
    }
}