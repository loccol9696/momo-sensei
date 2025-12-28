package com.example.be.service;

import com.example.be.dto.request.ModuleRequest;
import com.example.be.dto.response.ModuleResponse;
import com.example.be.entity.Folder;
import com.example.be.entity.FolderModule;
import com.example.be.entity.Module;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.ModuleMapper;
import com.example.be.repository.FolderModuleRepository;
import com.example.be.repository.FolderRepository;
import com.example.be.repository.ModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleService {

    AuthService authService;
    ModuleRepository moduleRepository;
    FolderRepository folderRepository;
    FolderModuleRepository folderModuleRepository;
    ModuleMapper moduleMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public ModuleResponse createModule(Authentication authentication, Long folderId , ModuleRequest request) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(folderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        String encodedPassword = null;

        if(request.getPassword() != null && !request.getPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        Module module = Module.builder()
                .name(request.getName())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .password(encodedPassword)
                .user(user)
                .build();

        moduleRepository.save(module);

        FolderModule folderModule = FolderModule.builder()
                .folder(folder)
                .module(module)
                .build();

        folderModuleRepository.save(folderModule);

        return moduleMapper.toModuleResponse(module);
    }

    @Transactional
    public ModuleResponse updateModule(Authentication authentication, Long id , ModuleRequest request) {
        User user = authService.validateUser(authentication);


        Module module = moduleRepository.findById(id).orElseThrow(
                () -> new BusinessException("Học phần không tồn tại", 404)
        );

        validateUpdateModulePermission(module, user, request.getPassword());

        if ((Objects.equals(module.getUser().getId(), user.getId()) && request.getNewPassword() != null)) {
            if (request.getNewPassword().isEmpty()) {
                module.setPassword(null);
            } else if (!request.getNewPassword().isBlank()) {
                module.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }
        }

        module.setName(request.getName());
        module.setDescription(request.getDescription() != null ? request.getDescription() : "");

        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }

    private void validateUpdateModulePermission(Module module, User user, String providedPassword) {
        if (module.getUser().getId().equals(user.getId())) {
            return;
        }

        if (module.getPassword() == null) {
            throw new BusinessException("Học phần này chỉ chủ sở hữu mới có quyền sửa", 403);
        }

        if (providedPassword == null || !passwordEncoder.matches(providedPassword, module.getPassword())) {
            throw new BusinessException("Mật khẩu chỉnh sửa không chính xác", 403);
        }
    }
}
