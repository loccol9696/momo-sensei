package com.example.be.service;

import com.example.be.dto.request.CheckAnswerRequest;
import com.example.be.dto.response.*;
import com.example.be.entity.Card;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.repository.CardRepository;
import com.example.be.utils.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudyService {

    AuthService authService;
    CardRepository cardRepository;
    CardService cardService;

    @Transactional(readOnly = true)
    public List<WriteQuestionResponse> getWriteQuestions(
            Authentication authentication, Long moduleId, boolean isStarred
    ) {
        List<CardResponse> allCards = cardService.getCards(authentication, moduleId, isStarred, true);

        return allCards.stream()
                .map(card -> WriteQuestionResponse.builder()
                        .cardId(card.getId())
                        .question(card.getDefinition())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CheckAnswerResponse checkAnswer(
            Authentication authentication, CheckAnswerRequest request
    ) {
        User user = authService.validateUser(authentication);

        Card card = cardRepository.findByIdAndIsDeletedFalse(request.getCardId())
                .orElseThrow(() -> new BusinessException("Thẻ không tồn tại", 404));

        if (!Objects.equals(card.getModule().getUser().getId(), user.getId())) {
            throw new BusinessException("Bạn không có quyền học thẻ này", 403);
        }

        String correctTerm = StringUtils.normalize(card.getTerm());
        String userInput = StringUtils.normalize(request.getUserAnswer());

        boolean isCorrect = correctTerm.equals(userInput);

        return CheckAnswerResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(card.getTerm())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChoiceQuestionResponse> getChoiceQuestions(
            Authentication authentication, Long moduleId, boolean isStarred
    ) {
        List<CardResponse> allCards = cardService.getCards(authentication, moduleId, isStarred, true);

        if (allCards.size() < 4) {
            throw new BusinessException("Cần ít nhất 4 thẻ để tạo bài trắc nghiệm", 400);
        }

        return allCards.stream().map(targetCard -> {
            String correctAnswer = targetCard.getTerm();

            List<String> distractors = allCards.stream()
                    .filter(c -> !c.getId().equals(targetCard.getId()))
                    .map(CardResponse::getTerm)
                    .distinct()
                    .collect(Collectors.toList());

            Collections.shuffle(distractors);

            List<String> options = new ArrayList<>(distractors.subList(0, 3));
            options.add(correctAnswer);
            Collections.shuffle(options);

            return ChoiceQuestionResponse.builder()
                    .cardId(targetCard.getId())
                    .question(targetCard.getDefinition())
                    .options(options)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchGameResponse> getMatchGame(
            Authentication authentication, Long moduleId, int level, boolean isStarred
    ) {
        List<CardResponse> allCards = cardService.getCards(authentication, moduleId, isStarred, false);
        int total = allCards.size();

        int limit = 4 + (level - 1) * 2;
        limit = Math.min(limit, 20);

        int offset = (level - 1) * (level + 2);

        if (offset >= total) {
            throw new BusinessException("Bạn đã hoàn thành tất cả các màn chơi!", 400);
        }

        int end = Math.min(offset + limit, total);
        List<CardResponse> gameCards = allCards.subList(offset, end);

        List<MatchGameResponse> elements = new ArrayList<>();
        for (CardResponse card : gameCards) {
            elements.add(
                    MatchGameResponse.builder()
                            .cardId(card.getId())
                            .content(card.getTerm())
                            .type("TERM")
                            .build()
            );

            elements.add(
                    MatchGameResponse.builder()
                            .cardId(card.getId())
                            .content(card.getDefinition())
                            .type("TERM")
                            .build()
            );
        }

        Collections.shuffle(elements);
        return elements;
    }
}
