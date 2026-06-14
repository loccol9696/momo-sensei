package com.example.be.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.be.dto.request.ChangePasswordRequest;
import com.example.be.dto.request.ProfileUpdateRequest;
import com.example.be.dto.response.ProfileResponse;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.UserMapper;
import com.example.be.repository.UserRepository;
import com.example.be.utils.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    UserMapper userMapper;
    AuthService authService;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinary;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Authentication authentication) {
        User user = authService.validateUser(authentication);

        return userMapper.toProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", 404));

        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(Authentication authentication, ProfileUpdateRequest request) {
        User user = authService.validateUser(authentication);

        if (StringUtils.isValidString(request.getFullName())) {
            user.setFullName(request.getFullName());
        }

        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            try {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                        request.getAvatarFile().getBytes(),
                        ObjectUtils.asMap("folder", "user_avatars")
                );

                String cloudAvatarUrl = (String) uploadResult.get("secure_url");

                user.setAvatar(cloudAvatarUrl);

            } catch (IOException e) {
                throw new RuntimeException("Gặp lỗi trong quá trình upload ảnh lên Cloudinary", e);
            }
        }

        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        User user = authService.validateUser(authentication);

        if (!StringUtils.isValidString(user.getPassword())) {
            throw new BusinessException("Tài khoản đăng nhập bằng Google không thể sử dụng tính năng này!", 400);
        }

        if (!StringUtils.isValidString(request.getOldPassword()) ||
                !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác!", 400);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
}