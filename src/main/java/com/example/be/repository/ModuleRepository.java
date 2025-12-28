package com.example.be.repository;

import com.example.be.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    Optional<Module> findByIdAndIsDeleted(Long id, Boolean isDeleted);
    Page<Module> findByFolder_IdAndUser_IdAndIsDeletedAndNameContainingIgnoreCase(
            Long folderId,
            Long userId,
            Boolean isDeleted,
            String name,
            Pageable pageable
    );
}
