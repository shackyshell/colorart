package com.shacky.colorart.controllers;

import com.shacky.colorart.data.PaintingImage;
import com.shacky.colorart.services.PaintingImageService;
import com.shacky.colorart.services.WindowsFileHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.Optional;

@RestController
//@RequestMapping("/api/editor")
public class ImageEditorController {

    @Autowired
    private PaintingImageService paintingImageService;

    @Autowired
    private WindowsFileHelperService windowsFileHelperService;

    @PostMapping("/submit")
    public ResponseEntity<?> handleImageSubmit(@RequestParam("imageUrl") String imageUrl) {
        try {
            PaintingImage imageEntity = paintingImageService.saveImageFromUrl(imageUrl, Optional.empty(), Optional.empty());
            return ResponseEntity.ok(imageEntity);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to load image from URL.");
        }
    }

    @PostMapping("/api/editor/invert")
    public ResponseEntity<?> handleInvert(@RequestParam("imageUrl") String imageUrl) {
        try {
            PaintingImage imageEntity = paintingImageService.handleInvert(imageUrl);
            return ResponseEntity.ok(imageEntity);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to invert image from URL.");
        }
    }

    @PostMapping("/api/editor/hue")
    public ResponseEntity<?> handleInvert(
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam("color")  String colorHex
    ) {
        try {
            // Convert the hex string to a Color object
            Color color = Color.decode(colorHex);
            PaintingImage imageEntity = paintingImageService.handleChangeHue(imageUrl, color);
            return ResponseEntity.ok(imageEntity);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to invert image from URL.");
        }
    }

    @GetMapping("/api/editor/{imageUrl}")
    public PaintingImage getImageByUrl(@PathVariable String imageUrl) {
        return paintingImageService.getImageByUrl(imageUrl);
    }
}
