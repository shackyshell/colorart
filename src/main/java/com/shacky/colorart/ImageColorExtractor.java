package com.shacky.colorart;

import org.springframework.stereotype.Service;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

@Service
public class ImageColorExtractor {

    public List<String> extractDominantColors(String imageUrl) throws IOException {
        BufferedImage img = ImageIO.read(new URL(imageUrl));

        // Logic to find dominant colors (simplified for brevity)
        List<String> dominantColors = new ArrayList<>();
        dominantColors.add("#FF5733"); // Example color
        dominantColors.add("#33FF57");
        dominantColors.add("#3357FF");

        return dominantColors;
    }
}
