package com.shacky.colorart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PaintingImageRepository extends JpaRepository<PaintingImage, Long> {
    List<PaintingImage> findByColorsContaining(String color);

    // Custom query to find paintings by matching colors
    @Query("SELECT p FROM PaintingImage p JOIN p.colors c WHERE c IN :colors")
    List<PaintingImage> findByColorsIn(@Param("colors") List<String> colors);
}
