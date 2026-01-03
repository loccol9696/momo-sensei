package com.example.be.repository;

import com.example.be.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByIdAndIsDeletedFalse(Long cardId);

    int countByModuleIdAndIsDeletedFalse(Long moduleId);

    @Modifying
    @Query("UPDATE Card c SET c.orderIndex = c.orderIndex - 1 " +
            "WHERE c.module.id = :moduleId " +
            "AND c.isDeleted = false " +
            "AND c.orderIndex > :deletedIndex")
    void reorderIndicesAfterDeletion(Long moduleId, Integer deletedIndex);

    List<Card> findAllByModuleIdAndIsDeletedFalseOrderByOrderIndexAsc(Long moduleId);

    List<Card> findAllByModuleIdAndIsDeletedFalseAndIsStarredTrueOrderByOrderIndexAsc(Long moduleId);

    @Modifying
    @Query("UPDATE Card c SET c.orderIndex = :newIndex WHERE c.id = :cardId AND c.module.id = :moduleId")
    void updateOrderIndex(Long cardId, Integer newIndex, Long moduleId);
}
