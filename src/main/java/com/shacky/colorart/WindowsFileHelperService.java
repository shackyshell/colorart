package com.shacky.colorart;

import org.springframework.stereotype.Service;

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

}
