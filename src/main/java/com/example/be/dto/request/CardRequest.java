package com.example.be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRequest {
    @NotBlank(message = "Thuật ngữ không được để trống")
    @Size(max = 255, message = "Thuật ngữ không quá 255 ký tự")
    String term;

    @NotBlank(message = "Định nghĩa không được để trống")
    @Size(max = 1000, message = "Định nghĩa không quá 1000 ký tự")
    String definition;

    String imageUrl;
}
