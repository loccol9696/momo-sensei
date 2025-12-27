package com.example.be.service;

import com.example.be.dto.request.FolderRequest;
import com.example.be.dto.response.FolderResponse;
import com.example.be.entity.Folder;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.FolderMapper;
import com.example.be.repository.FolderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FolderService {

    AuthService authService;
    FolderRepository folderRepository;
    FolderMapper folderMapper;

    @Transactional
    public void createFolder(
            Authentication authentication, FolderRequest folderRequest
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = Folder.builder()
                .name(folderRequest.getName())
                .user(user)
                .build();

        folderRepository.save(folder);
    }

    @Transactional
    public void updateFolder(
            Authentication authentication, Long id, FolderRequest folderRequest
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(id, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        folder.setName(folderRequest.getName());

        folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(
            Authentication authentication, Long id
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(id, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        folder.setIsDeleted(true);

        folderRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public Page<FolderResponse> getFolders(
            Authentication authentication, String search, int page, int size
    ) {
        User user = authService.validateUser(authentication);

        Sort sort =  Sort.by(Sort.Direction.DESC, "usedAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Folder> folderPage = folderRepository.findByUser_IdAndIsDeletedAndNameContainingIgnoreCase(
                user.getId(), false, search, pageable
        );

        return  folderPage.map(folderMapper::toFolderResponse);
    }

    @Transactional(readOnly = true)
    public FolderResponse getFolder(Authentication authentication, Long id) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(id, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        return folderMapper.toFolderResponse(folder);
    }

    @Transactional(readOnly = true)
    public Page<FolderResponse> getTrashFolders(
            Authentication authentication, String search, int page, int size
    ) {
        User user = authService.validateUser(authentication);

        Sort sort = Sort.by(Sort.Direction.DESC, "deletedAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Folder> folderPage = folderRepository.findByUser_IdAndIsDeletedAndNameContainingIgnoreCase(
                user.getId(), true, search, pageable
        );

        return  folderPage.map(folderMapper::toFolderResponse);
    }

    @Transactional(readOnly = true)
    public FolderResponse getTrashFolder(Authentication authentication, Long id) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(id, user.getId(), true)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        return folderMapper.toFolderResponse(folder);
    }

    @Transactional
    public void restoreFolder(
            Authentication authentication, Long id
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeleted(id, user.getId(), true)
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        folder.setIsDeleted(false);

        folderRepository.save(folder);
    }

    @Transactional
    public void deleteExpiredFolders(LocalDateTime threshold) {
        List<Folder> expired = folderRepository.findAllByIsDeletedTrueAndDeletedAtBefore(threshold);

        folderRepository.deleteAll(expired);
    }
}
