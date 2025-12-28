package com.example.be.repository;

import com.example.be.entity.FolderModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderModuleRepository extends JpaRepository<FolderModule, Long> {
    Optional<FolderModule> findByFolder_User_IdAndModule_IdAndIsDeleted(Long userId, Long moduleId, Boolean isDeleted);

}
