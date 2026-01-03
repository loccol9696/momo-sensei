package com.example.be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardImportRequest {
    @NotBlank(message = "Nội dung thô không được để trống")
    String rawText;

    @NotBlank(message = "Ký tự phân tách thẻ không được để trống")
    String cardSeparator;

    @NotBlank(message = "Ký tự phân tách thuật ngữ không được để trống")
    String termSeparator;
}
