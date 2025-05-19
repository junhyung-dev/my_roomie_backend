package com.everytime.Hackathon2025.Service;

import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.LoginRequestDto;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Exception.InvalidPasswordException;
import com.everytime.Hackathon2025.Exception.NotLoggedInException;
import com.everytime.Hackathon2025.Exception.UserNotFoundException;
import com.everytime.Hackathon2025.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    //현재 사용자 정보
    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            throw new NotLoggedInException();
            // unAuthorized
        }
        return (User) session.getAttribute("user");
    }

    public UserSimpleResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (!user.getPassword().equals(loginRequestDto.getPassword())) {
            throw new InvalidPasswordException();

        }

        HttpSession session = request.getSession(); //session이 존재하지 않으면 새로운 세션 생성
        session.setAttribute("user", user);

        return userService.convertUserToSimpleDto(user, user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false를 전달하면, 현재 session이 존재하지 않으면 null을 반환
        if (session != null) {
            session.invalidate();  // 세션 비활성화
        }
    }
}
