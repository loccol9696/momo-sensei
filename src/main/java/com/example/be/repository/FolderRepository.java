package com.example.be.repository;

import com.example.be.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByIdAndUser_IdAndIsDeleted(Long id, Long userId, boolean isDeleted);

    Page<Folder> findByUser_IdAndIsDeletedAndNameContainingIgnoreCase(
            Long userId, boolean isDeleted, String name, Pageable pageable
    );

    List<Folder> findAllByIsDeletedTrueAndDeletedAtBefore(LocalDateTime threshold);


}
