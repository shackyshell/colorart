package com.shacky.colorart;
import javax.persistence.*;
import java.util.List;

@Entity
public class PaintingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ElementCollection
    private List<String> colors; // Store hex colors as strings

    // Getters, setters, constructors
}
