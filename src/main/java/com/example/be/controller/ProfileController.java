package com.example.be.controller;

import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.UserResponse;
import com.example.be.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Controller", description = "Quản lý thông tin và hồ sơ người dùng")
public class ProfileController {

    ProfileService profileService;

    @GetMapping
    @Operation(summary = "Lấy thông tin cá nhân của người dùng hiện tại")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        UserResponse response = profileService.getUser(authentication);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Lấy thông tin người dùng thành công")
                        .build()
        );
    }

}
