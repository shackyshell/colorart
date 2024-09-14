package com.shacky.colorart;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColorRangeGenerator {

    // Function to return a list of hex colors within 30% saturation difference
    public List<String> generateSaturationRange(String hexColor) {
        List<String> colorRange = new ArrayList<>();

        // Step 1: Convert hex to HSL
        float[] hsl = hexToHSL(hexColor);
        float hue = hsl[0];
        float saturation = hsl[1];
        float lightness = hsl[2];

        // Step 2: Define the range for saturation (±30%)
        float lowerBound = Math.max(0, saturation - 0.3f);
        float upperBound = Math.min(1, saturation + 0.3f);

        // Step 3: Generate colors within this range
        for (float s = lowerBound; s <= upperBound; s += 0.05f) {
            String hex = hslToHex(hue, s, lightness);
            colorRange.add(hex);
        }

        return colorRange;
    }

    // Convert hex color to HSL
    float[] hexToHSL(String hex) {
        Color color = Color.decode(hex);
        float[] hsl = new float[3];
        rgbToHSL(color.getRed(), color.getGreen(), color.getBlue(), hsl);
        return hsl;
    }

    // RGB to HSL conversion
    private void rgbToHSL(int r, int g, int b, float[] hsl) {
        float red = r / 255f;
        float green = g / 255f;
        float blue = b / 255f;

        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;

        // Calculate lightness
        float lightness = (max + min) / 2f;
        hsl[2] = lightness;

        // If max == min, it means there is no saturation, it's a shade of gray
        if (max == min) {
            hsl[0] = 0;
            hsl[1] = 0;
            return;
        }

        // Calculate saturation
        if (lightness < 0.5f) {
            hsl[1] = delta / (max + min);
        } else {
            hsl[1] = delta / (2f - max - min);
        }

        // Calculate hue
        if (max == red) {
            hsl[0] = (green - blue) / delta + (green < blue ? 6 : 0);
        } else if (max == green) {
            hsl[0] = (blue - red) / delta + 2;
        } else {
            hsl[0] = (red - green) / delta + 4;
        }

        hsl[0] /= 6;
    }

    // HSL to hex color conversion
    private String hslToHex(float hue, float saturation, float lightness) {
        float r, g, b;

        if (saturation == 0) {
            // Achromatic (grey)
            r = g = b = lightness;
        } else {
            float q = lightness < 0.5 ? lightness * (1 + saturation) : lightness + saturation - lightness * saturation;
            float p = 2 * lightness - q;
            r = hueToRGB(p, q, hue + 1f / 3f);
            g = hueToRGB(p, q, hue);
            b = hueToRGB(p, q, hue - 1f / 3f);
        }

        // Convert to hex
        return String.format("#%02x%02x%02x", (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    // Helper for converting hue to RGB
    private float hueToRGB(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6f) return p + (q - p) * 6 * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6;
        return p;
    }


    // Calculate Euclidean distance between two HSL colors
    public static double colorDistance(float[] hsl1, float[] hsl2) {
        double dh = Math.min(Math.abs(hsl1[0] - hsl2[0]), 1 - Math.abs(hsl1[0] - hsl2[0]));
        double ds = hsl1[1] - hsl2[1];
        double dl = hsl1[2] - hsl2[2];
        return Math.sqrt(dh * dh + ds * ds + dl * dl);
    }
}
