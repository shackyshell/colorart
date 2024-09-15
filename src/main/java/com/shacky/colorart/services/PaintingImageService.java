package com.shacky.colorart.services;

import com.shacky.colorart.ColorRangeGenerator;
import com.shacky.colorart.ImageColorExtractor;
import com.shacky.colorart.data.PaintingImage;
import com.shacky.colorart.data.PaintingImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
            saveImageFromUrl(imageUrl, distinguishableColorsThresholdPercentage, maxSwatches);
        }
    }

    @Transactional
    public PaintingImage saveImageFromUrl(String imageUrl, Optional<Integer> distinguishableColorsThresholdPercentage, Optional<Integer> maxSwatches) throws IOException {
        // Check if image already exists
        PaintingImage existingImage = repository.findByImageUrl(imageUrl);
        if (existingImage != null) {
            return existingImage;
        }

        String base64Image = WindowsFileHelperService.imageToBase64(imageUrl);

        List<String> colors = colorExtractor.extractDominantColors(
                imageUrl,
                distinguishableColorsThresholdPercentage.orElse(defaultDistinguishableColorsThresholdPercentage),
                maxSwatches.orElse(10)
        );
        PaintingImage image = new PaintingImage();
        image.setImageUrl(imageUrl);
        image.setColors(colors);
        image.setBase64Image(base64Image);

        return repository.save(image);
    }

    public List<PaintingImage> getAllImages() {
        return repository.findAll();
    }

    public PaintingImage getImageByUrl(String imageUrl) {
        return repository.findByImageUrl(imageUrl);
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

    public PaintingImage handleInvert(String imageUrl) throws IOException {
//        try {
            PaintingImage paintingImage = getImageByUrl(imageUrl);
            BufferedImage image = ImageIO.read(new URL(imageUrl));
        BufferedImage image2 = ImageIO.read(new URL(imageUrl));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image2, "PNG", baos);
        baos.flush();
        byte[] byteArray = baos.toByteArray();
        baos.close();

        if (image != null) {
            // Step 2: Get the pixel colors
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = image.getRGB(x, y);
                    int invertedRgb = (0xFFFFFFFF - rgb) | 0xFF000000;
                    image2.setRGB(x, y, invertedRgb);
                }
            }
        }


            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image2, "PNG", os);
            byte[] byteArray2 = os.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(byteArray2);
            os.close();

            paintingImage.setBase64Image(base64Image);

//            PaintingImage result = paintingImage;
//            byte[] decodedBytes = Base64.getDecoder().decode(result.getBase64Image());
//            BufferedImage invertedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

            return paintingImage;
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
    }
}
