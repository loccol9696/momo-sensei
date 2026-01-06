package com.example.be.dto.request;

import com.example.be.enums.CardType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchGameRequest {

    @NotNull(message = "ID thẻ thứ nhất không được để trống")
    Long firstCardId;

    @NotNull(message = "Loại thẻ thứ nhất (TERM hoặc DEFINITION) không được để trống")
    CardType firstCardType;

    @NotNull(message = "ID thẻ thứ hai không được để trống")
    Long secondCardId;

    @NotNull(message = "Loại thẻ thứ hai (TERM hoặc DEFINITION) không được để trống")
    CardType secondCardType;
}