package com.example.be.service;

import com.example.be.dto.response.UserResponse;
import com.example.be.entity.User;
import com.example.be.mapper.UserMapper;
import com.example.be.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    AuthService authService;
    UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getUser(Authentication authentication) {
        User user = authService.validateUser(authentication);
        return userMapper.toUserResponse(user);
    }

}
