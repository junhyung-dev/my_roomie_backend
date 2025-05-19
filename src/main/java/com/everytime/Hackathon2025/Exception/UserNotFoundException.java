package com.everytime.Hackathon2025.Exception;

/** 아이디(또는 username)가 존재하지 않을 때 발생 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("존재하지 않는 회원입니다.");
    }
}