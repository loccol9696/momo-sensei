package com.example.be.service;

import com.example.be.dto.request.CreateModuleRequest;
import com.example.be.dto.request.UpdateModuleRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleService {

    AuthService authService;
    ModuleRepository moduleRepository;
    FolderRepository folderRepository;
    FolderModuleRepository folderModuleRepository;
    ModuleMapper moduleMapper;

    @Transactional
    public ModuleResponse createModule(Authentication authentication, CreateModuleRequest request) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(request.getFolderId(), user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        Module module = Module.builder()
                .name(request.getName())
                .description(request.getDescription() != null ? request.getDescription() : "")
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
    public ModuleResponse updateModule(Authentication authentication, Long id , UpdateModuleRequest request) {
        User user = authService.validateUser(authentication);

        FolderModule folderModule = folderModuleRepository.findByFolder_User_IdAndModule_IdAndIsDeleted(user.getId(), id, false)
                .orElseThrow(() -> new BusinessException("Module không tồn tại trong thư mục của người dùng", 404));

        Module module = folderModule.getModule();

        module.setName(request.getName());
        module.setDescription(request.getDescription() != null ? request.getDescription() : "");

        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }
}
