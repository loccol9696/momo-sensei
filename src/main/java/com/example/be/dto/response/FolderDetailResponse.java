package com.example.be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FolderDetailResponse {
    Long id;
    String name;
    List<ModuleResponse> modules;
    LocalDateTime usedAt;
    LocalDateTime deletedAt;
}

