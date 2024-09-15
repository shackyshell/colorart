package com.shacky.colorart.controllers;

import com.shacky.colorart.data.PaintingImage;
import com.shacky.colorart.services.PaintingImageService;
import com.shacky.colorart.services.WindowsFileHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class PaintingImageController {

    @Autowired
    private PaintingImageService paintingImageService;

    @Autowired
    private WindowsFileHelperService windowsFileHelperService;

    @PostMapping("/scrape")
    public String scrapeAndStoreImages(
            @RequestParam Optional<String> url,
                                       @RequestParam Optional<Integer> distinguishableColorsThresholdPercentage,
            @RequestParam Optional<Integer> maxSwatches) throws IOException {
        paintingImageService.scrapeAndStoreImages(url, distinguishableColorsThresholdPercentage, maxSwatches);
        return "Images scraped and saved successfully!";
    }

    @GetMapping("/all-images")
    public List<PaintingImage> getAllImages() {
        return paintingImageService.getAllImages();
    }

    @GetMapping
    public List<PaintingImage> getImagesByColor(@RequestParam Optional<String> color, @RequestParam Optional<Integer> similarityThresholdPercentage) {
        return paintingImageService.findImagesByColor(color, similarityThresholdPercentage);
    }

    @GetMapping("/open-images")
    public String getCommandToOpenAllTheImagesFoundByColor(@RequestParam Optional<String> color, @RequestParam Optional<Integer> similarityThresholdPercentage) {
        List<PaintingImage> images = paintingImageService.findImagesByColor(color, similarityThresholdPercentage);
        return windowsFileHelperService.getCommandToOpenAllTheImagesInTheBrowser(images);
    }

    // Endpoint to return all dominant colors
    @GetMapping("/colors")
    public List<String> getAllDominantColors() {
        return paintingImageService.getAllDominantColors();
    }

    //Endpoint to return all color groups
    @GetMapping("/color-groups")
    public Map<Integer, List<String>> getAllDominantColorsGrouped() {
        List<String> colors = paintingImageService.getAllDominantColors();
        return paintingImageService.groupColors(colors);
    }
}
