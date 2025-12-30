package com.example.be.dto.response;

import com.example.be.enums.ModulePermission;
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
    String ownerName;
    String ownerAvatar;
    ModulePermission permission;
    Integer totalCards;
}
