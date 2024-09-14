package com.shacky.colorart;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaintingImageService {

    Integer defaultDistinguishableColorsThresholdPercentage = 20;
    Integer similarityThreshold = 20;
    double similarityThresholdPercentage = 20.0/100;

    @Autowired
    private PaintingImageRepository repository;

    @Autowired
    private WebScraperService scraperService;

    @Autowired
    private ImageColorExtractor colorExtractor;

    @Autowired
    private ColorRangeGenerator generator;

    public void setSimilarityThreshold(Integer similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
        this.similarityThresholdPercentage = (float)similarityThreshold/100;
    }

    @Transactional
    public void scrapeAndStoreImages(Optional<String> url, Optional<Integer> distinguishableColorsThresholdPercentage, Optional<Integer> maxSwatches) throws IOException {
        List<String> imageUrls = scraperService.scrapeImageUrls(url);

        for (String imageUrl : imageUrls) {
            List<String> colors = colorExtractor.extractDominantColors(
                    imageUrl,
                    distinguishableColorsThresholdPercentage.orElse(defaultDistinguishableColorsThresholdPercentage),
                    maxSwatches.orElse(10)
            );

            PaintingImage image = new PaintingImage();
            image.setImageUrl(imageUrl);
            image.setColors(colors);

            repository.save(image);
        }
    }

    public List<PaintingImage> getAllImages() {
        return repository.findAll();
    }

    public List<PaintingImage> findImagesByColor(Optional<String> hexColor, Optional<Integer> similarityThreshold) {
        if (similarityThreshold.isPresent()) {
            setSimilarityThreshold(similarityThreshold.get());
        }
        if (!hexColor.isPresent()) {
            return getAllImages();
        }
        float[] searchedHslColor =generator.hexToHSL(hexColor.get());
        List<String> hexColors = getAllDominantColors();
        List<float[]> hslColors = new ArrayList<>();
        for (String hex : hexColors) {
            float[] hslColor = generator.hexToHSL(hex);
            if (generator.colorDistance(searchedHslColor, hslColor) < similarityThresholdPercentage) {
                hslColors.add(hslColor);
            }
        }
        List<String> colors = hslColors.stream().map(color -> generator.hslToHex(color[0], color[1], color[2])).toList();
        return repository.findByColorsIn(colors);
    }

    // Method to get all dominant colors from all paintings
    public List<String> getAllDominantColors() {
        // Fetch all paintings
        List<PaintingImage> paintings = repository.findAll();

        // Extract colors from all paintings and return unique colors as a Set
        return paintings.stream()
                .flatMap(painting -> painting.getColors().stream())
                .collect(Collectors.toList());
    }

    // Group colors by similarity
    public Map<Integer, List<String>> groupColors(List<String> hexColors) {
        List<float[]> hslColors = new ArrayList<>();
        for (String hex : hexColors) {
            hslColors.add(generator.hexToHSL(hex));
        }

        Map<Integer, List<String>> colorGroups = new HashMap<>();
        boolean[] grouped = new boolean[hexColors.size()];
        int groupId = 0;

        for (int i = 0; i < hexColors.size(); i++) {
            if (!grouped[i]) {
                List<String> group = new ArrayList<>();
                group.add(hexColors.get(i));
                grouped[i] = true;

                for (int j = 0; j < hexColors.size(); j++) {
                    if (i != j && !grouped[j] && generator.colorDistance(hslColors.get(i), hslColors.get(j)) < similarityThresholdPercentage) {
                        group.add(hexColors.get(j));
                        grouped[j] = true;
                    }
                }

                colorGroups.put(groupId++, group);
            }
        }

        return colorGroups;
    }
}
