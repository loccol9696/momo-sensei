package com.example.be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FolderResponse {
    Long id;
    String name;
    LocalDateTime usedAt;
    LocalDateTime deletedAt;
}
