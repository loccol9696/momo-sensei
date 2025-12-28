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
import org.springframework.data.domain.Page;
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

    @PutMapping("/modules/{moduleId}")
    @Operation(
            summary = "Cập nhật module"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            Authentication authentication, @PathVariable Long moduleId, @Valid @RequestBody ModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.updateModule(authentication, moduleId, request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .data(moduleResponse)
                        .message("Cập nhật module thành công")
                        .build()
        );
    }

    @DeleteMapping("/modules/{moduleId}")
    @Operation(
            summary = "Xóa module"
    )
    public ResponseEntity<ApiResponse<Void>> deleteModule(
            Authentication authentication, @PathVariable Long moduleId
    ) {
        moduleService.deleteModule(authentication, moduleId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Học phần đã được chuyển vào thùng rác")
                        .build()
        );
    }

    @GetMapping("/folders/{folderId}/modules")
    @Operation(
            summary = "Lấy danh sách module theo thư mục"
    )
    public ResponseEntity<ApiResponse<Page<ModuleResponse>>> getModulesByFolder(
            Authentication authentication,
            @PathVariable Long folderId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ModuleResponse> moduleResponse = moduleService.getModulesByFolder(authentication, folderId, search, page, size);

        return ResponseEntity.ok(
                ApiResponse.<Page<ModuleResponse>>builder()
                        .data(moduleResponse)
                        .message("Lấy danh sách học phần thành công")
                        .build()
        );
    }
}
