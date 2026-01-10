package com.example.be.service;

import com.example.be.dto.request.ProfileUpdateRequest;
import com.example.be.dto.response.ProfileResponse;
import com.example.be.entity.User;
import com.example.be.mapper.UserMapper;
import com.example.be.repository.UserRepository;
import com.example.be.utils.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    UserMapper userMapper;
    AuthService authService;
    PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Authentication authentication) {
        User user = authService.validateUser(authentication);

        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(Authentication authentication, ProfileUpdateRequest request) {
        User user = authService.validateUser(authentication);

        if(StringUtils.isValidString(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if(StringUtils.isValidString(request.getFullName())) {
            user.setFullName(request.getFullName());
        }

        if(StringUtils.isValidString(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        return  userMapper.toProfileResponse(user);
    }
}