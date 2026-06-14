package com.example.be.service;

import com.example.be.dto.request.CardImportRequest;
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

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.example.be.entity.UserCardStar;
import com.example.be.repository.UserCardStarRepository;
import com.example.be.enums.ModulePermission;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CardService {

    CardRepository cardRepository;
    ModuleRepository moduleRepository;
    CardMapper cardMapper;
    AuthService authService;
    UserCardStarRepository userCardStarRepository;

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

        if (!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền chỉnh sửa thẻ này", 403);
        }

        card.setTerm(request.getTerm());
        card.setDefinition(request.getDefinition());
        card.setImageUrl(request.getImageUrl());

        CardResponse response = cardMapper.toCardResponse(card);
        response.setStarred(userCardStarRepository.existsByUserIdAndCardId(user.getId(), cardId));
        return response;
    }

    @Transactional
    public void deleteCard(Authentication authentication, Long cardId) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        if (!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền xóa thẻ này", 403);
        }

        card.setDeleted(true);

        cardRepository.reorderIndicesAfterDeletion(
                card.getModule().getId(),
                card.getOrderIndex()
        );
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getCards(
            Authentication authentication, Long moduleId, boolean isStarred, boolean shuffle
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndIsDeleted(moduleId, false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại", 404));

        if (module.getPermission() == ModulePermission.PRIVATE && !Objects.equals(module.getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền truy cập học phần này", 403);
        }

        List<Card> cards;
        if (isStarred) {
            cards = cardRepository.findAllStarredCardsByModuleIdAndUserId(module.getId(), user.getId());
        } else {
            cards = cardRepository.findAllByModuleIdAndIsDeletedFalseOrderByOrderIndexAsc(module.getId());
        }

        List<CardResponse> cardResponses = mapCardsToResponses(cards, user);

        if (shuffle) {
            Collections.shuffle(cardResponses);
        }

        return cardResponses;
    }

    @Transactional(readOnly = true)
    public CardResponse getCard(
            Authentication authentication, Long cardId
    ) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        Module module = card.getModule();
        if (module.getPermission() == ModulePermission.PRIVATE && !Objects.equals(module.getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền truy cập thẻ này", 403);
        }

        CardResponse response = cardMapper.toCardResponse(card);
        response.setStarred(userCardStarRepository.existsByUserIdAndCardId(user.getId(), cardId));
        return response;
    }

    @Transactional
    public void toggleStar(Authentication authentication, Long cardId) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(cardId)
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        Module module = card.getModule();
        if (module.getPermission() == ModulePermission.PRIVATE && !Objects.equals(module.getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền thay đổi trạng thái yêu thích của thẻ này", 403);
        }

        Optional<UserCardStar> starOpt = userCardStarRepository.findByUserIdAndCardId(user.getId(), cardId);
        if (starOpt.isPresent()) {
            userCardStarRepository.delete(starOpt.get());
        } else {
            UserCardStar star = UserCardStar.builder()
                    .user(user)
                    .card(card)
                    .build();
            userCardStarRepository.save(star);
        }
    }

    private List<CardResponse> mapCardsToResponses(List<Card> cards, User user) {
        if (cards == null) return Collections.emptyList();

        Set<Long> starredCardIds = userCardStarRepository.findAllByUserId(user.getId())
                .stream()
                .map(star -> star.getCard().getId())
                .collect(Collectors.toSet());

        return cards.stream().map(card -> {
            CardResponse response = cardMapper.toCardResponse(card);
            response.setStarred(starredCardIds.contains(card.getId()));
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void reorderCards(
            Authentication authentication, Long moduleId, List<Long> cardIds
    ) {
        User user = authService.validateUser(authentication);

        if (!moduleRepository.existsByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false)) {
            throw new BusinessException("Học phần không tồn tại hoặc bạn không có quyền sở hữu.", 403);
        }

        List<Card> currentCards = cardRepository.findAllByModuleIdAndIsDeletedFalseOrderByOrderIndexAsc(moduleId);

        if (currentCards.size() != cardIds.size()) {
            throw new BusinessException("Số lượng thẻ không chính xác.", 400);
        }

        Map<Long, Card> cardMap = currentCards.stream()
                .collect(Collectors.toMap(Card::getId, card -> card));

        for (int i = 0; i < cardIds.size(); i++) {
            Card card = cardMap.get(cardIds.get(i));

            if (card == null) {
                throw new BusinessException("Danh sách chứa thẻ không thuộc học phần này.", 400);
            }

            card.setOrderIndex(i);
        }

        cardRepository.saveAll(currentCards);
    }

    @Transactional
    public List<CardResponse> importCards(
            Authentication authentication,
            Long moduleId,
            CardImportRequest request
    ) {
        User user = authService.validateUser(authentication);

        Module module = moduleRepository.findByIdAndUser_IdAndIsDeleted(moduleId, user.getId(), false)
                .orElseThrow(() -> new BusinessException("Học phần không tồn tại hoặc bạn không có quyền sở hữu", 404));

        int nextIndex = cardRepository.countByModuleIdAndIsDeletedFalse(moduleId);

        String[] cardEntries = request.getRawText().split(Pattern.quote(request.getCardSeparator()));

        List<Card> cardsToSave = new ArrayList<>();

        for (String entry : cardEntries) {
            String[] parts = entry.split(Pattern.quote(request.getTermSeparator()), 2);

            if (parts.length < 2) continue;

            String term = parts[0].trim();
            String definition = parts[1].trim();

            if (term.isEmpty() || definition.isEmpty()) continue;

            Card card = Card.builder()
                    .term(term)
                    .definition(definition)
                    .orderIndex(nextIndex++)
                    .module(module)
                    .build();

            cardsToSave.add(card);
        }

        if (cardsToSave.isEmpty()) {
            throw new BusinessException("Dữ liệu nhập vào không hợp lệ hoặc trống", 400);
        }

        List<Card> savedCards = cardRepository.saveAll(cardsToSave);

        return cardMapper.toCardResponseList(savedCards);
    }
}
