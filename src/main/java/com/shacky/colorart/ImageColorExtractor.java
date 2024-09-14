package com.shacky.colorart;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

@Service
public class ImageColorExtractor {

    public List<String> extractDominantColors(String imageUrl) throws IOException {
//        try {
        // Step 1: Download and read the image from the provided URL
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        if (image != null) {
            // Step 2: Get the pixel colors
            Map<Integer, Integer> colorMap = new HashMap<>();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = image.getRGB(x, y);
                    colorMap.put(rgb, colorMap.getOrDefault(rgb, 0) + 1);
                }
            }

            List<String> distinguishableColors = ColorRangeGenerator.getDistinguishableColors(colorMap.keySet().stream().map(this::toHex).toList(), 20.0);
//            System.out.println("jul" + distinguishableColors);
//            // Step 3: Sort by frequency of color occurrence and pick the top 3
            List<Map.Entry<Integer, Integer>> sortedColors = colorMap.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .filter((colorEntry) -> distinguishableColors.contains( toHex(colorEntry.getKey())))
                    .limit(10) // top 3 dominant colors
                    .collect(Collectors.toList());

            // Step 4: Convert the RGB color values to hex and return the list
            List<String> dominantColors = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : sortedColors) {
                String hexColor = toHex(entry.getKey());
                dominantColors.add(hexColor);
            }

            return dominantColors;
        }
        return new ArrayList<>();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
    }

    // Convert integer RGB value to hex color code
    private String toHex(int rgb) {
        Color color = new Color(rgb);
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
