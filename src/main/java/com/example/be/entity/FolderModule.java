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
@Table(name = "folder_modules")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FolderModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    Folder folder;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    Module module;

    LocalDateTime usedAt;
}
