package com.example.be.service;

import com.example.be.dto.request.CardRequest;
import com.example.be.dto.response.CardResponse;
import com.example.be.entity.Card;
import com.example.be.entity.Module;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.CardMapper;
import com.example.be.repository.CardRepository;
import com.example.be.repository.ModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardService {

    CardRepository cardRepository;
    ModuleRepository moduleRepository;
    CardMapper cardMapper;
    AuthService authService;

    @Transactional
    public CardResponse createCard(
            Authentication authentication, Long moduleId, CardRequest request
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        int nextIndex = cardRepository.countByModuleIdAndIsDeletedFalse(moduleId);

        Card card = Card.builder()
                .term(request.getTerm())
                .definition(request.getDefinition())
                .imageUrl(request.getImageUrl())
                .orderIndex(nextIndex)
                .module(module)
                .build();

        Card savedCard = cardRepository.save(card);

        return cardMapper.toCardResponse(savedCard);
    }

    @Transactional
    public CardResponse updateCard(
            Authentication authentication, Long cardId, CardRequest request
    ) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        if(!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền chỉnh sửa thẻ này", 403);
        }

        card.setTerm(request.getTerm());
        card.setDefinition(request.getDefinition());
        card.setImageUrl(request.getImageUrl());

        return cardMapper.toCardResponse(card);
    }

    @Transactional
    public void deleteCard(Authentication authentication, Long cardId) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        if(!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền xóa thẻ này", 403);
        }

        card.setDeleted(true);

        cardRepository.reorderIndicesAfterDeletion(
                card.getModule().getId(),
                card.getOrderIndex()
        );
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getCards(Authentication authentication, Long moduleId) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        List<Card> cards = cardRepository.findAllByModuleIdAndIsDeletedFalseOrderByOrderIndexAsc(module.getId());

        return cardMapper.toCardResponseList(cards);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> shuffleCards(Authentication authentication, Long moduleId) {
        List<CardResponse> cards = getCards(authentication, moduleId);

        Collections.shuffle(cards);

        return cards;
    }

    @Transactional
    public void toggleStar(Authentication authentication, Long cardId) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        if (!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền thay đổi trạng thái yêu thích của thẻ này", 403);
        }

        card.setStarred(!card.isStarred());
    }

    @Transactional
    public void reorderCards(
            Authentication authentication, Long moduleId, List<Long> cardIds
    ) {
        User user = authService.validateUser(authentication);

        if (!moduleRepository.existsByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false)) {
            throw new BusinessException("Học phần không tồn tại hoặc bạn không có quyền sở hữu.", 403);
        }

        for (int i = 0; i < cardIds.size(); i++) {
            cardRepository.updateOrderIndex(cardIds.get(i), i, moduleId);
        }
    }
}
