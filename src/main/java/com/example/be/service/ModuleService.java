package com.example.be.service;

import com.example.be.dto.request.ModuleRequest;
import com.example.be.dto.response.ModuleResponse;
import com.example.be.entity.Folder;
import com.example.be.entity.Module;
import com.example.be.entity.User;
import com.example.be.enums.ModulePermission;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.ModuleMapper;
import com.example.be.repository.FolderRepository;
import com.example.be.repository.ModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleService {

    AuthService authService;
    ModuleRepository moduleRepository;
    FolderRepository folderRepository;
    ModuleMapper moduleMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public ModuleResponse createModule(Authentication authentication, Long folderId , ModuleRequest request) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(folderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        String encodedPassword = null;

        if(request.getPermission() == ModulePermission.PASSWORD) {
            if(request.getPassword() == null || request.getPassword().isBlank()) {
                throw new BusinessException("Mật khẩu không được để trống", 400);
            }
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        Module module = Module.builder()
                .name(request.getName())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .password(encodedPassword)
                .permission(request.getPermission())
                .user(user)
                .folder(folder)
                .build();

        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }

    @Transactional
    public ModuleResponse updateModule(Authentication authentication, Long moduleId , ModuleRequest request) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false).orElseThrow(
                () -> new BusinessException("Học phần không tồn tại", 404)
        );

        if (!Objects.equals(module.getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền cập nhật học phần này", 403);
        }

        module.setName(request.getName());
        module.setDescription(request.getDescription() != null ? request.getDescription() : "");
        module.setPermission(request.getPermission());

        if(request.getPermission() == ModulePermission.PASSWORD) {
            if(request.getPassword() != null && !request.getPassword().isBlank()) {
                module.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            else if(module.getPassword() == null) {
                throw new BusinessException("Mật khẩu không được để trống", 400);
            }
        } else {
            module.setPassword(null);
        }

        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }

    public void deleteModule(Authentication authentication, Long moduleId) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false).orElseThrow(
                () -> new BusinessException("Học phần không tồn tại hoặc đã bị xóa trước đó", 404)
        );

        if (!Objects.equals(module.getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền xóa học phần này", 403);
        }

        module.setIsDeleted(true);
        module.setDeletedAt(LocalDateTime.now());

        moduleRepository.save(module);
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> getModulesByFolder(
            Authentication authentication, Long folderId, String search , int page, int size
    ) {
        User user =  authService.validateUser(authentication);

        folderRepository.findByIdAndUser_IdAndIsDeleted(folderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        String searchKey = (search == null) ? "" : search.trim();

        Sort sort = Sort.by(Sort.Direction.DESC, "usedAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Module> modulePage = moduleRepository
                .findByFolder_IdAndUser_IdAndIsDeletedAndNameContainingIgnoreCase(
                        folderId, user.getId(), false , searchKey , pageable
                );

        return modulePage.map(moduleMapper::toModuleResponse);
    }
}
