package com.shacky.colorart.data;

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

    @Column(unique = true, nullable = false)
    private String imageUrl;

    @ElementCollection
    private List<String> colors; // Store hex colors as strings

    @Lob
    @Column(nullable = false)
    private String base64Image;
}
