package com.shacky.colorart;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaintingImageRepository extends JpaRepository<PaintingImage, Long> {
    List<PaintingImage> findByColorsContaining(String color);
}
