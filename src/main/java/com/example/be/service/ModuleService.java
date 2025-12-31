package com.example.be.service;

import com.example.be.dto.request.ModuleRequest;
import com.example.be.dto.response.ModuleDetailResponse;
import com.example.be.dto.response.ModuleResponse;
import com.example.be.entity.Card;
import com.example.be.entity.Folder;
import com.example.be.entity.Module;
import com.example.be.entity.User;
import com.example.be.enums.ModulePermission;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.ModuleMapper;
import com.example.be.repository.CardRepository;
import com.example.be.repository.FolderRepository;
import com.example.be.repository.ModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    CardRepository cardRepository;

    @NonFinal
    @Value("${frontend.url}")
    String frontendUrl;

    @Transactional
    public ModuleResponse createModule(
            Authentication authentication, Long folderId, ModuleRequest request
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(folderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        String encodedPassword = null;

        if (request.getPermission() == ModulePermission.PASSWORD) {
            if (request.getPassword() == null || request.getPassword().isBlank()) {
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
    public ModuleResponse updateModule(
            Authentication authentication, Long moduleId, ModuleRequest request
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false).orElseThrow(
                () -> new BusinessException("Học phần không tồn tại hoặc đã bị xóa trước đó", 404)
        );

        module.setName(request.getName());
        module.setDescription(request.getDescription() != null ? request.getDescription() : "");
        module.setPermission(request.getPermission());

        if (request.getPermission() == ModulePermission.PASSWORD) {
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                module.setPassword(passwordEncoder.encode(request.getPassword()));
            } else if (module.getPassword() == null) {
                throw new BusinessException("Mật khẩu không được để trống", 400);
            }
        } else {
            module.setPassword(null);
        }

        moduleRepository.save(module);

        return moduleMapper.toModuleResponse(module);
    }

    @Transactional
    public void deleteModule(
            Authentication authentication, Long moduleId
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false).orElseThrow(
                () -> new BusinessException("Học phần không tồn tại hoặc đã bị xóa trước đó", 404)
        );

        module.setIsDeleted(true);
        module.setDeletedAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> getModulesByFolder(
            Authentication authentication, Long folderId, String search, int page, int size
    ) {
        User user = authService.validateUser(authentication);

        folderRepository.findByIdAndUser_IdAndIsDeleted(folderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        String searchKey = (search == null) ? "" : search.trim();

        Sort sort = Sort.by(Sort.Direction.DESC, "usedAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Module> modulePage = moduleRepository
                .findByFolder_IdAndUser_IdAndIsDeletedAndNameContainingIgnoreCase(
                        folderId, user.getId(), false, searchKey, pageable
                );

        return modulePage.map(moduleMapper::toModuleResponse);
    }

    @Transactional
    public ModuleDetailResponse getModule(Authentication authentication, Long moduleId, String password) {
        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        User currentUser = authService.getCurrentUser(authentication);

        validateModuleAccess(module, currentUser, password);

        ModuleDetailResponse response = moduleMapper.toModuleDetailResponse(module);

        if (currentUser != null) {
            boolean isNewView = module.getViewedByUsers().add(currentUser);
            response.setLiked(module.getLikedByUsers().contains(currentUser));

            if (isNewView) {
                int currentViews = (response.getTotalViews() == null) ? 0 : response.getTotalViews();
                response.setTotalViews(currentViews + 1);
            }

            if (Objects.equals(currentUser.getId(), module.getUser().getId())) {
                module.setUsedAt(LocalDateTime.now());
            }
        }
        return response;
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> getTrashModules(
            Authentication authentication, String search, int page, int size
    ) {
        User user = authService.validateUser(authentication);

        Sort sort = Sort.by(Sort.Direction.DESC, "deletedAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Module> modulePage = moduleRepository.findByUser_IdAndIsDeletedAndNameContainingIgnoreCase(
                user.getId(), true, search, pageable
        );

        return modulePage.map(moduleMapper::toModuleResponse);
    }

    @Transactional(readOnly = true)
    public ModuleDetailResponse getTrashModule(
            Authentication authentication, Long moduleId
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), true)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại trong thùng rác", 404));

        return moduleMapper.toModuleDetailResponse(module);
    }

    @Transactional
    public void restoreModule(
            Authentication authentication, Long moduleId
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), true)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại trong thùng rác", 404));

        module.setIsDeleted(false);
        module.setDeletedAt(null);
    }

    @Transactional
    public void deleteExpiredModules(LocalDateTime threshold) {
        moduleRepository.deleteExpiredModules(threshold);
    }

    @Transactional
    public ModuleResponse cloneModule(
            Authentication authentication,
            Long moduleId,
            Long targetFolderId,
            String password
    ) {
        User user = authService.validateUser(authentication);

        Module original = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần gốc không tồn tại", 404));

        validateModuleAccess(original, user, password);

        Folder targetFolder = folderRepository.findByIdAndUser_IdAndIsDeleted(targetFolderId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục đích không tồn tại hoặc bạn không có quyền", 404));

        Module clonedModule = Module.builder()
                .name(original.getName() + " (Bản sao)")
                .description(original.getDescription())
                .permission(ModulePermission.PRIVATE)
                .user(user)
                .folder(targetFolder)
                .usedAt(LocalDateTime.now())
                .build();

        Module savedModule = moduleRepository.save(clonedModule);

        if (original.getCards() != null && !original.getCards().isEmpty()) {
            List<Card> newCards = original.getCards().stream()
                    .map(card -> Card.builder()
                            .term(card.getTerm())
                            .definition(card.getDefinition())
                            .imageUrl(card.getImageUrl())
                            .module(savedModule)
                            .build())
                    .toList();

            cardRepository.saveAll(newCards);
        }

        return moduleMapper.toModuleResponse(savedModule);
    }

    private void validateModuleAccess(Module module, User user, String password) {
        boolean isOwner = user != null && Objects.equals(module.getUser().getId(), user.getId());

        if (isOwner) return;

        if (module.getPermission() == ModulePermission.PRIVATE) {
            throw new BusinessException("Bạn không có quyền truy cập học phần này", 403);
        }

        if (module.getPermission() == ModulePermission.PASSWORD) {
            if (password == null || password.isBlank()) {
                throw new BusinessException("Học phần yêu cầu mật khẩu để truy cập", 403);
            }
            if (!passwordEncoder.matches(password, module.getPassword())) {
                throw new BusinessException("Mật khẩu không đúng", 403);
            }
        }
    }

    @Transactional(readOnly = true)
    public String exportModule(
            Authentication authentication,
            Long moduleId,
            String password,
            String termSeparator,
            String cardSeparator
    ) {
        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        User user = authService.validateUser(authentication);

        validateModuleAccess(module, user, password);

        StringBuilder exportBuilder = new StringBuilder();

        List<Card> cards = module.getCards();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            exportBuilder.append(card.getTerm())
                    .append(termSeparator)
                    .append(card.getDefinition());
            if (i < cards.size() - 1) {
                exportBuilder.append(cardSeparator);
            }
        }

        return exportBuilder.toString();
    }

    @Transactional(readOnly = true)
    public String getShareLink(Long moduleId) {
        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        if (module.getPermission() == ModulePermission.PRIVATE) {
            throw new BusinessException("Không thể chia sẻ học phần đang ở chế độ riêng tư", 403);
        }

        return frontendUrl + "/modules/" + moduleId;
    }

    @Transactional
    public void toggleLike(Authentication authentication, Long moduleId) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        if (module.getPermission() != ModulePermission.PUBLIC) {
            throw new BusinessException("Chỉ có thể thích các học phần công khai", 403);
        }

        if (module.getLikedByUsers().contains(user)) {
            module.getLikedByUsers().remove(user);
        } else {
            module.getLikedByUsers().add(user);
        }
    }
}
