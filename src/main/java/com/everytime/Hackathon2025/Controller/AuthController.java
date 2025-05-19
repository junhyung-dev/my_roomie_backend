package com.everytime.Hackathon2025.Controller;


import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.LoginRequestDto;
import com.everytime.Hackathon2025.Dto.UserRegistrationRequestDto;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Service.AuthService;
import com.everytime.Hackathon2025.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//로그인, 회원가입 기능 controller
@RestController
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserSimpleResponseDto> registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = new User(
                userRegistrationRequestDto.getUsername(),
                userRegistrationRequestDto.getPassword(),
                userRegistrationRequestDto.getName()
        );
        UserSimpleResponseDto savedUser = userService.saveUser(user);

        return ResponseEntity.ok(savedUser);
    }

    //로그인
    @PostMapping("/auth/login")
    public ResponseEntity<UserSimpleResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        UserSimpleResponseDto userSimpleResponseDto = authService.login(loginRequestDto, request);
        return ResponseEntity.ok(userSimpleResponseDto);
    }

    //로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/profile")
    public ResponseEntity<UserSimpleResponseDto> profile(HttpServletRequest request) {
        User currentUser = authService.getCurrentUser(request);

        UserSimpleResponseDto userSimpleResponseDto = userService.convertUserToSimpleDto(currentUser, currentUser);
        return ResponseEntity.ok(userSimpleResponseDto);
    }
}
