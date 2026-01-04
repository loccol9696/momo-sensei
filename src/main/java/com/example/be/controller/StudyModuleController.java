package com.example.be.controller;

import com.example.be.dto.request.WriteTestRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.WriteTestResponse;
import com.example.be.service.StudyModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Study Mode", description = "Các API hỗ trợ chế độ học tập")
public class StudyModuleController {

    StudyModuleService studyModuleService;

    @PostMapping("/write")
    @Operation(summary = "Kiểm tra tự luận: Hiện nghĩa - Nhập Term")
    public ResponseEntity<ApiResponse<WriteTestResponse>> checkTerm(
            Authentication authentication,
            @Valid @RequestBody WriteTestRequest request
    ) {
        WriteTestResponse response = studyModuleService.checkTermAnswer(authentication, request);

        return ResponseEntity.ok(
                ApiResponse.<WriteTestResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Đã xử lý kết quả kiểm tra")
                        .build()
        );
    }
}
