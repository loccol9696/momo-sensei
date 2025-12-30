package com.example.be.dto.response;

import com.example.be.enums.ModulePermission;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleDetailResponse {
    Long id;
    String name;
    String description;
    Long ownerId;
    String ownerName;
    String ownerAvatar;
    Integer totalCards;
    Integer totalLikes;
    Integer totalViews;
    ModulePermission permission;
    @Builder.Default
    Boolean liked = false;
    List<CardResponse> cards;
}
