package com.example.be.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleResponse {
    Long id;
    String name;
    String description;
    Long ownerId;
    Long ownerName;
    Integer totalCards;
}
