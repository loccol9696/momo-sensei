package com.example.be.dto.response;

import com.example.be.enums.CardType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchGameResponse {
    Long cardId;
    String content;
    CardType cardType;
}