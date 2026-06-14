package com.example.be.repository;

import com.example.be.entity.UserCardStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardStarRepository extends JpaRepository<UserCardStar, Long> {
    Optional<UserCardStar> findByUserIdAndCardId(Long userId, Long cardId);
    boolean existsByUserIdAndCardId(Long userId, Long cardId);
    void deleteByUserIdAndCardId(Long userId, Long cardId);
    List<UserCardStar> findAllByUserId(Long userId);
}
