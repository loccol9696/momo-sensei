package com.example.be.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "folders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "NVARCHAR(255)")
    String name;

    LocalDateTime usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder.Default
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.usedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.usedAt = LocalDateTime.now();

        if (Boolean.TRUE.equals(this.isDeleted)) {
            if (this.deletedAt == null) {
                this.deletedAt = LocalDateTime.now();
            }
        } else {
            this.deletedAt = null;
        }
    }
}
