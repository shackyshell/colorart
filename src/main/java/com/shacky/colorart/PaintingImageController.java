package com.shacky.colorart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/images")
public class PaintingImageController {

    @Autowired
    private PaintingImageService paintingImageService;

    @PostMapping("/scrape")
    public String scrapeAndStoreImages() throws IOException {
        paintingImageService.scrapeAndStoreImages();
        return "Images scraped and saved successfully!";
    }

    @GetMapping
    public List<PaintingImage> getImagesByColor(@RequestParam String color) {
        return paintingImageService.findImagesByColor(color);
    }

    // Endpoint to return all dominant colors
    @GetMapping("/colors")
    public Set<String> getAllDominantColors() {
        return paintingImageService.getAllDominantColors();
    }
}
