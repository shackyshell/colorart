package com.shacky.colorart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class PaintingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ElementCollection
    private List<String> colors; // Store hex colors as strings

    // Getters, setters, constructors
}
