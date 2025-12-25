package com.example.be.repository;

import com.example.be.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByIdAndUser_IdAndIsDeletedFalse(Long id, Long userId);

    Page<Folder> findByUserIdAndIsDeletedFalseAndNameContainingIgnoreCase(
            Long userId, String name, Pageable pageable
    );
}
