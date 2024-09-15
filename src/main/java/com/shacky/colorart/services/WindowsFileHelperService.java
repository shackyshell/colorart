package com.shacky.colorart.services;

import com.shacky.colorart.data.PaintingImage;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;

@Service
public class WindowsFileHelperService {

    public String getCommandToOpenAllTheImagesInTheBrowser(List<PaintingImage> paintingImages) {
        List<String> imageUrls = paintingImages.stream().map(paintingImage -> removeTrailingAfterQuestionMark(paintingImage.getImageUrl())).toList();
        String result = String.join("\" & explorer \"", imageUrls);
        return "explorer \"" + result + "\"";
    }

    public static String removeTrailingAfterQuestionMark(String input) {
        int index = input.indexOf('?');
        if (index != -1) {
            return input.substring(0, index);
        }
        return input; // Return the original string if '?' is not found
    }

    public static String imageToBase64(String imageUrl) throws IOException {
        URL urlAsURL = new URL(imageUrl);
        InputStream inputStream = urlAsURL.openStream();
        byte[] imageBytes = inputStream.readAllBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public static void base64ToImage(String base64Image, String outputFilePath) {
        try {
            // Decode the Base64 string to a byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Write the byte array to a file (output image)
            try (OutputStream outputStream = new FileOutputStream(outputFilePath)) {
                outputStream.write(imageBytes);
                System.out.println("Image successfully written to " + outputFilePath);
            }
        } catch (Exception e) {
            System.out.println("Error converting Base64 string to image: " + e.getMessage());
        }
    }
}
