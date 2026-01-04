package com.example.be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WriteTestRequest {
    @NotNull(message = "ID thẻ không được để trống")
    Long cardId;

    @NotBlank(message = "Câu trả lời không được để trống")
    String userAnswer;
}
