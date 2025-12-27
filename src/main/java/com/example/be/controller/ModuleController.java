package com.example.be.controller;

import com.example.be.dto.request.CreateModuleRequest;
import com.example.be.dto.request.UpdateModuleRequest;
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
@RequestMapping("/api/modules")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
        name = "Module Management",
        description = "API xử lý các chức năng liên quan đến quản lý module"
)
public class ModuleController {

    ModuleService moduleService;

    @PostMapping
    @Operation(
            summary = "Tạo module mới"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(
            Authentication authentication, @Valid @RequestBody CreateModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.createModule(authentication, request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .data(moduleResponse)
                        .message("Tạo module thành công")
                        .build()
        );
    }

    @PostMapping("/{id}")
    @Operation(
            summary = "Cập nhật module"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody UpdateModuleRequest request
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
