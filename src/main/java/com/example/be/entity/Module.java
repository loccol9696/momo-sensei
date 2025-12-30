package com.example.be.entity;

import com.example.be.enums.ModulePermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "modules")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    String name;

    @Column(columnDefinition = "NVARCHAR(1000)")
    String description;

    String password;

    LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    ModulePermission permission;

    @Builder.Default
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    Folder folder;

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Card> cards = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "module_likes",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> likedByUsers = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "module_views",
            joinColumns = {@JoinColumn(name = "module_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    Set<User> viewedByUsers = new HashSet<>();

    @Formula("(select count(*) from cards c where c.module_id = id)")
    Integer totalCards;

    @Formula("(select count(*) from module_likes ml where ml.module_id = id)")
    Integer totalLikes;

    @Formula("(select count(*) from module_views mv where mv.module_id = id)")
    Integer totalViews;
}
