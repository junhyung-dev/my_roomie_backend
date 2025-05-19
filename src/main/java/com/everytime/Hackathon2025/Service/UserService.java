package com.everytime.Hackathon2025.Service;


import com.everytime.Hackathon2025.Domain.User;
import com.everytime.Hackathon2025.Dto.UserSimpleResponseDto;
import com.everytime.Hackathon2025.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserSimpleResponseDto saveUser(User newUser) {
        // 중복 회원 검증
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new IllegalStateException("중복되는 username입니다.");
        }

        userRepository.save(newUser);
        return convertUserToSimpleDto(newUser, newUser);
    }

    public UserSimpleResponseDto convertUserToSimpleDto(User currentUser, User targetUser) {
        return new UserSimpleResponseDto(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getName()
        );
    }
}
