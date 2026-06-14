package com.example.be.controller;

import com.example.be.dto.request.ChangePasswordRequest;
import com.example.be.dto.request.ProfileUpdateRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.ProfileResponse;
import com.example.be.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
        name = "Profile",
        description = "API xử lý thông tin cá nhân của người dùng"
)
public class ProfileController {

    ProfileService profileService;

    @GetMapping
    @Operation(
            summary = "Lấy thông tin cá nhân"
    )
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        ProfileResponse response = profileService.getProfile(authentication);

        ApiResponse<ProfileResponse> apiResponse = ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Lấy thông tin cá nhân thành công")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Lấy thông tin cá nhân của người khác bằng userId"
    )
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileById(@PathVariable Long userId) {
        ProfileResponse response = profileService.getProfileById(userId);

        ApiResponse<ProfileResponse> apiResponse = ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Lấy thông tin cá nhân thành công")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Cập nhật thông tin cá nhân"
    )
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @ModelAttribute ProfileUpdateRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(authentication, request);

        ApiResponse<ProfileResponse> apiResponse = ApiResponse.<ProfileResponse>builder()
                .success(true)
                .message("Cập nhật thông tin cá nhân thành công")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/password")
    @Operation(
            summary = "Thay đổi mật khẩu"
    )
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        profileService.changePassword(authentication, request);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .success(true)
                .message("Thay đổi mật khẩu thành công")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}