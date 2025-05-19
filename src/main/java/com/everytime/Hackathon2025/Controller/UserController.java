package com.everytime.Hackathon2025.Controller;


import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.UserRegistrationRequestDto;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }





}



