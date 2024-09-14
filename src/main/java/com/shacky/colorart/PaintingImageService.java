package com.shacky.colorart;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class PaintingImageService {

    @Autowired
    private PaintingImageRepository repository;

    @Autowired
    private WebScraperService scraperService;

    @Autowired
    private ImageColorExtractor colorExtractor;

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
        return repository.findByColorsContaining(hexColor);
    }
}
