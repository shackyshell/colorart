package com.shacky.colorart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/images")
public class PaintingImageController {

    @Autowired
    private PaintingImageService paintingImageService;

    @Autowired
    private WindowsFileHelperService windowsFileHelperService;

    @PostMapping("/scrape")
    public String scrapeAndStoreImages() throws IOException {
        paintingImageService.scrapeAndStoreImages();
        return "Images scraped and saved successfully!";
    }

    @GetMapping
    public List<PaintingImage> getImagesByColor(@RequestParam String color) {
        return paintingImageService.findImagesByColor(color);
    }

    @GetMapping("/open-images")
    public String getCommandToOpenAllTheImagesFoundByColor(@RequestParam String color) {
        List<PaintingImage> images = paintingImageService.findImagesByColor(color);
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
