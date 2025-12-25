package com.example.be.entity;

import com.example.be.enums.AuthProvider;
import com.example.be.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String email;

    String password;

    @Column(columnDefinition = "NVARCHAR(255)")
    String fullName;

    String avatar;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    AuthProvider authProvider;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    @Builder.Default
    Boolean active = true;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
