package com.shacky.colorart;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColorRangeGenerator {



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
    String hslToHex(float hue, float saturation, float lightness) {
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


    // Main function to get distinguishable colors
    public static List<String> getDistinguishableColors(List<String> hexColors, double threshold) {
        List<String> result = new ArrayList<>();

        for (String color : hexColors) {
            boolean isDistinguishable = true;

            // Compare the current color with all colors already in the result list
            for (String existingColor : result) {
                if (calculateDeltaE(color, existingColor) < threshold) {
                    isDistinguishable = false;
                    break;
                }
            }

            // If the color is distinguishable from all previous ones, add it to the result
            if (isDistinguishable) {
                result.add(color);
            }
        }
        return result;
    }

    // Function to calculate Delta E (CIE76) between two hex colors
    public static double calculateDeltaE(String hex1, String hex2) {
        double[] lab1 = rgbToLab(hexToRgb(hex1));
        double[] lab2 = rgbToLab(hexToRgb(hex2));

        return Math.sqrt(Math.pow(lab2[0] - lab1[0], 2) +
                Math.pow(lab2[1] - lab1[1], 2) +
                Math.pow(lab2[2] - lab1[2], 2));
    }

    // Convert hex color to RGB
    public static int[] hexToRgb(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    // Convert RGB to LAB color space
    public static double[] rgbToLab(int[] rgb) {
        // Normalize RGB values
        double r = rgb[0] / 255.0;
        double g = rgb[1] / 255.0;
        double b = rgb[2] / 255.0;

        // Apply gamma correction
        r = (r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : (r / 12.92);
        g = (g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : (g / 12.92);
        b = (b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : (b / 12.92);

        // Convert to XYZ color space
        double x = r * 0.4124564 + g * 0.3575761 + b * 0.1804375;
        double y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750;
        double z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041;

        // Normalize for D65 illuminant
        x /= 0.95047;
        y /= 1.00000;
        z /= 1.08883;

        // Convert XYZ to LAB
        x = (x > 0.008856) ? Math.pow(x, 1.0 / 3) : (7.787 * x + 16.0 / 116);
        y = (y > 0.008856) ? Math.pow(y, 1.0 / 3) : (7.787 * y + 16.0 / 116);
        z = (z > 0.008856) ? Math.pow(z, 1.0 / 3) : (7.787 * z + 16.0 / 116);

        double l = 116 * y - 16;
        double a = 500 * (x - y);
        double bValue = 200 * (y - z);

        return new double[]{l, a, bValue};
    }
}
