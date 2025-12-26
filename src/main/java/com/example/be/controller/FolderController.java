package com.example.be.controller;

import com.example.be.dto.request.FolderRequest;
import com.example.be.dto.response.ApiResponse;
import com.example.be.dto.response.FolderResponse;
import com.example.be.service.FolderService;
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
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
        name = "Folder Management",
        description = "API xử lý các chức năng liên quan đến quản lý thư mục"
)
public class FolderController {

    FolderService folderService;

    @PostMapping
    @Operation(
            summary = "Tạo thư mục mới"
    )
    public ResponseEntity<ApiResponse<Void>> createFolder(
            Authentication authentication, @Valid @RequestBody FolderRequest folderRequest
    ) {
        folderService.createFolder(authentication, folderRequest);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Tạo thư mục thành công")
                        .build()
        );
    }

    @PostMapping("/update/{id}")
    @Operation(
            summary = "Cập nhật thông tin thư mục"
    )
    public ResponseEntity<ApiResponse<Void>> updateFolder(
            Authentication authentication, @PathVariable Long id , @Valid @RequestBody FolderRequest folderRequest
    ) {
        folderService.updateFolder(authentication, id, folderRequest);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Cập nhật thư mục thành công")
                        .build()
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Xóa thư mục"
    )
    public ResponseEntity<ApiResponse<Void>> deleteFolder(
            Authentication authentication, @PathVariable Long id
    ) {
        folderService.deleteFolder(authentication, id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa thư mục thành công")
                        .build()
        );
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách thư mục"
    )
    public ResponseEntity<ApiResponse<Page<FolderResponse>>> getFolders(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FolderResponse> response = folderService.getFolders(
                authentication, search, page, size
        );
        return ResponseEntity.ok(
                ApiResponse.<Page<FolderResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách thư mục thành công")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/trash")
    @Operation(
            summary = "Lấy danh sách thư mục trong thùng rác"
    )
    public ResponseEntity<ApiResponse<Page<FolderResponse>>> getTrashFolders(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<FolderResponse> response = folderService.getTrashFolders(
                authentication, search, page, size
        );
        return ResponseEntity.ok(
                ApiResponse.<Page<FolderResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách thư mục trong thùng rác thành công")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/restore/{id}")
    @Operation(
            summary = "Khôi phục thư mục từ thùng rác"
    )
    public ResponseEntity<ApiResponse<Void>> restoreFolder(
            Authentication authentication, @PathVariable Long id
    ) {
        folderService.restoreFolder(authentication, id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Khôi phục thư mục thành công")
                        .build()
        );
    }
}
