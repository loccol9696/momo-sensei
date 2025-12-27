package com.example.be.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Card> cards = new ArrayList<>();
}
