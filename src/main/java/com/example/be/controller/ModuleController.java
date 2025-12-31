package com.example.be.controller;

import com.example.be.dto.request.ModuleRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.ModuleDetailResponse;
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
        description = "API xử lý các chức năng liên quan đến quản lý học phần"
)
public class ModuleController {

    ModuleService moduleService;

    @PostMapping("/folders/{folderId}/modules")
    @Operation(
            summary = "Tạo học phần mới"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(
            Authentication authentication,  @PathVariable Long folderId , @Valid @RequestBody ModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.createModule(authentication, folderId , request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .success(true)
                        .data(moduleResponse)
                        .message("Tạo học phần thành công")
                        .build()
        );
    }

    @PutMapping("/modules/{moduleId}")
    @Operation(
            summary = "Cập nhật học phần"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            Authentication authentication, @PathVariable Long moduleId, @Valid @RequestBody ModuleRequest request
    ) {
        ModuleResponse moduleResponse = moduleService.updateModule(authentication, moduleId, request);

        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .data(moduleResponse)
                        .message("Cập nhật học phần thành công")
                        .build()
        );
    }

    @DeleteMapping("/modules/{moduleId}")
    @Operation(
            summary = "Xóa học phần"
    )
    public ResponseEntity<ApiResponse<Void>> deleteModule(
            Authentication authentication, @PathVariable Long moduleId
    ) {
        moduleService.deleteModule(authentication, moduleId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Học phần đã được chuyển vào thùng rác")
                        .build()
        );
    }

    @GetMapping("/folders/{folderId}/modules")
    @Operation(
            summary = "Lấy danh sách học phần theo thư mục"
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
                        .success(true)
                        .data(moduleResponse)
                        .message("Lấy danh sách học phần thành công")
                        .build()
        );
    }

    @GetMapping("/modules/{moduleId}")
    @Operation(
            summary = "Lấy thông tin học phần"
    )
    public ResponseEntity<ApiResponse<ModuleDetailResponse>> getModule(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestParam (required = false) String password
    ) {
        ModuleDetailResponse moduleResponse = moduleService.getModule(authentication, moduleId, password);

        return ResponseEntity.ok(
                ApiResponse.<ModuleDetailResponse>builder()
                        .success(true)
                        .data(moduleResponse)
                        .message("Lấy thông tin học phần thành công")
                        .build()
        );
    }

    @GetMapping("/modules/trash")
    @Operation(
            summary = "Lấy danh sách học phần trong thùng rác"
    )
    public ResponseEntity<ApiResponse<Page<ModuleResponse>>> getTrashModules(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ModuleResponse> moduleResponse = moduleService.getTrashModules(authentication, search, page, size);

        return ResponseEntity.ok(
                ApiResponse.<Page<ModuleResponse>>builder()
                        .success(true)
                        .data(moduleResponse)
                        .message("Lấy danh sách học phần trong thùng rác thành công")
                        .build()
        );
    }

    @GetMapping("/modules/trash/{moduleId}")
    @Operation(
            summary = "Lấy thông tin học phần trong thùng rác"
    )
    public ResponseEntity<ApiResponse<ModuleDetailResponse>> getTrashModule(
            Authentication authentication,
            @PathVariable Long moduleId
    ) {
        ModuleDetailResponse moduleResponse = moduleService.getTrashModule(authentication, moduleId);

        return ResponseEntity.ok(
                ApiResponse.<ModuleDetailResponse>builder()
                        .success(true)
                        .data(moduleResponse)
                        .message("Lấy thông tin học phần trong thùng rác thành công")
                        .build()
        );
    }

    @PatchMapping("/modules/trash/{moduleId}/restore")
    @Operation(
            summary = "Khôi phục học phần từ thùng rác"
    )
    public ResponseEntity<ApiResponse<Void>> restoreModule(
            Authentication authentication,
            @PathVariable Long moduleId
    ) {
        moduleService.restoreModule(authentication, moduleId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Khôi phục học phần thành công")
                        .build()
        );
    }

    @PostMapping("/modules/{moduleId}/clone")
    @Operation(
            summary = "Sao chép học phần"
    )
    public ResponseEntity<ApiResponse<ModuleResponse>> cloneModule(
            Authentication authentication,
            @PathVariable Long moduleId,
            @RequestParam Long folderId,
            @RequestParam(required = false) String password
    ) {
        ModuleResponse response = moduleService.cloneModule(authentication, moduleId, folderId, password);
        return ResponseEntity.ok(
                ApiResponse.<ModuleResponse>builder()
                        .success(true)
                        .message("Sao chép học phần thành công")
                        .data(response)
                        .build()
        );
    }
}
