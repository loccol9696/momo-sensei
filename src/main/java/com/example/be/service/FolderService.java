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

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeletedFalse(id, user.getId())
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        folder.setName(folderRequest.getName());

        folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(
            Authentication authentication, Long id
    ) {
        User user = authService.validateUser(authentication);

        Folder folder = folderRepository.findByIdAndUser_IdAndIsDeletedFalse(id, user.getId())
                .orElseThrow(() -> new BusinessException("Thư mục không tồn tại", 404));

        folder.setIsDeleted(true);

        folderRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public Page<FolderResponse> getFolders(
            Authentication authentication, String search, int page, int size, String sortBy
    ) {
        User user = authService.validateUser(authentication);

        Sort sort;

        if ("name".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "name");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "usedAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Folder> folderPage = folderRepository.findByUserIdAndIsDeletedFalseAndNameContainingIgnoreCase(
                user.getId(), search, pageable
        );

        return  folderPage.map(folderMapper::toFolderResponse);
    }
}
