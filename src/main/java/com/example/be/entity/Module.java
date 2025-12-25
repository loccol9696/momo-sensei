package com.example.be.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String title;

    @Column(columnDefinition = "NVARCHAR(1000)")
    String description;
}
