package com.example.be.controller;

import com.example.be.dto.request.ModuleRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.ModuleResponse;
import com.example.be.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
        name = "Module Management",
        description = "API xử lý các chức năng liên quan đến quản lý module"
)
public class ModuleController {

    ModuleService moduleService;

    @PostMapping("/folders/{folderId}/modules")
    @Operation(
            summary = "Tạo module mới"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(
            Authentication authentication,  @PathVariable Long folderId , @Valid @RequestBody ModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.createModule(authentication, folderId , request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .data(moduleResponse)
                        .message("Tạo module thành công")
                        .build()
        );
    }

    @PutMapping("/modules/{id}")
    @Operation(
            summary = "Cập nhật module"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody ModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.updateModule(authentication, id, request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .data(moduleResponse)
                        .message("Cập nhật module thành công")
                        .build()
        );
    }

}
