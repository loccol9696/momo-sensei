package com.example.be.service;

import com.example.be.dto.request.WriteTestRequest;
import com.example.be.dto.response.WriteTestResponse;
import com.example.be.entity.Card;
import com.example.be.entity.User;
import com.example.be.exception.BusinessException;
import com.example.be.mapper.ModuleMapper;
import com.example.be.repository.CardRepository;
import com.example.be.repository.ModuleRepository;
import com.example.be.utils.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudyModuleService {

    AuthService authService;
    CardRepository cardRepository;

    @Transactional(readOnly = true)
    public WriteTestResponse checkTermAnswer(
            Authentication authentication, WriteTestRequest request
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

        return WriteTestResponse.builder()
                .isCorrect(isCorrect)
                .correctAnswer(card.getTerm())
                .build();
    }
}
