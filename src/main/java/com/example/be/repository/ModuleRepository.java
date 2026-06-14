package com.example.be.repository;

import com.example.be.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module,Long> {
    boolean existsByIdAndUser_IdAndIsDeleted(Long id, Long userId, Boolean isDeleted);

    Optional<Module> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    Optional<Module> findByIdAndUser_IdAndIsDeleted(Long id, Long userId, Boolean isDeleted);

    Page<Module> findByFolder_IdAndUser_IdAndIsDeletedAndNameContainingIgnoreCase(
            Long folderId,
            Long userId,
            Boolean isDeleted,
            String name,
            Pageable pageable
    );

    Page<Module> findByUser_IdAndIsDeletedAndNameContainingIgnoreCase(
            Long userId,
            Boolean isDeleted,
            String name,
            Pageable pageable
    );

    @Modifying
    @Query("DELETE FROM Module m WHERE m.isDeleted = true AND m.deletedAt <= :threshold")
    void deleteExpiredModules(@Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("UPDATE Module m SET m.isDeleted = :status, m.deletedAt = :time" +
            " WHERE m.folder.id = :folderId")
    void updateSoftDeleteByFolderId(Long folderId, boolean status , LocalDateTime time);

    @Query("SELECT m FROM Module m WHERE m.isDeleted = false " +
           "AND (m.permission = com.example.be.enums.ModulePermission.PUBLIC " +
           "OR m.permission = com.example.be.enums.ModulePermission.PASSWORD " +
           "OR m.user.id = :userId) " +
           "AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Module> searchModules(@Param("search") String search, @Param("userId") Long userId, Pageable pageable);
}
