package com.shacky.colorart;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PaintingImageService {

    @Autowired
    private PaintingImageRepository repository;

    @Autowired
    private WebScraperService scraperService;

    @Autowired
    private ImageColorExtractor colorExtractor;

    @Autowired
    private ColorRangeGenerator generator;

    @Transactional
    public void scrapeAndStoreImages() throws IOException {
        List<String> imageUrls = scraperService.scrapeImageUrls();

        for (String imageUrl : imageUrls) {
            List<String> colors = colorExtractor.extractDominantColors(imageUrl);

            PaintingImage image = new PaintingImage();
            image.setImageUrl(imageUrl);
            image.setColors(colors);

            repository.save(image);
        }
    }

    public List<PaintingImage> findImagesByColor(String hexColor) {
        List<String> colors = generator.generateSaturationRange(hexColor);
//        return repository.findByColorsContaining(hexColor);
        return repository.findByColorsIn(colors);
    }

    // Method to get all dominant colors from all paintings
    public Set<String> getAllDominantColors() {
        // Fetch all paintings
        List<PaintingImage> paintings = repository.findAll();

        // Extract colors from all paintings and return unique colors as a Set
        return paintings.stream()
                .flatMap(painting -> painting.getColors().stream())
                .collect(Collectors.toSet());
    }
}
