package com.shacky.colorart.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.shacky.colorart.ColorRangeGenerator;
import com.shacky.colorart.ImageColorExtractor;
import com.shacky.colorart.data.PaintingImage;
import com.shacky.colorart.data.PaintingImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

//@SpringBootTest
public class PaintingImageServiceTest {

    @Mock
    private PaintingImageRepository repository;

    @Mock
    private WebScraperService scraperService;

    @Mock
    private ImageColorExtractor colorExtractor;

    @Mock
    private ColorRangeGenerator generator;

    @InjectMocks
    private PaintingImageService paintingServiceUnderTest;

    String imageUrl = "http://example.com/image.jpg";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        PowerMockito.mockStatic(ImageIO.class);
//        mockStatic(ImageIO.class);
//        mockStatic(ImageIO.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testHandleInvertValidImage() throws IOException {
        // Arrange
//        String imageUrl = "https://apicollections.parismusees.paris.fr/sites/default/files/styles/thumbnail/collections/atoms/images/CAR/lpdp_81596-9.jpg?itok=hx7qaCGZ";
        // Create a small in-memory image (e.g., 2x2 image)
        BufferedImage testImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        testImage.setRGB(0, 0, 0x000000);  // Black
        testImage.setRGB(0, 1, 0xFFFFFF);  // White
        testImage.setRGB(1, 0, 0x0000FF);  // Blue
        testImage.setRGB(1, 1, 0xFF0000);  // Red

//        // Mock the ImageIO.read to return the in-memory image
        try (MockedStatic<ImageIO> mocked = mockStatic(ImageIO.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> ImageIO.read(new URL(imageUrl))).thenReturn(testImage);

            PaintingImage mockedPaintingImage = new PaintingImage();
            mockedPaintingImage.setImageUrl(imageUrl);
            mockedPaintingImage.setColors(List.of());
            when(repository.findByImageUrl(imageUrl)).thenReturn(mockedPaintingImage);

            // Act
            PaintingImage result = paintingServiceUnderTest.handleInvert(imageUrl);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getBase64Image());

//            byte[] decodedBytes2 = Base64.getDecoder().decode(testImage.getBase64Image());
//            BufferedImage invertedImage2 = ImageIO.read(new ByteArrayInputStream(decodedBytes2));

            // Decode Base64 and verify it’s an inverted image
            byte[] decodedBytes = Base64.getDecoder().decode(result.getBase64Image());
            BufferedImage invertedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

            assertEquals(0xFFFFFF, invertedImage.getRGB(0, 0)); // Black -> White
            assertEquals(0x000000, invertedImage.getRGB(0, 1)); // White -> Black
            assertEquals(0xFFFF00, invertedImage.getRGB(1, 0)); // Blue -> Yellow
            assertEquals(0x00FFFF, invertedImage.getRGB(1, 1)); // Red -> Cyan
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void testHandleInvertInvalidUrl() {
//        // Arrange
//        String invalidUrl = "invalid_url";
//
//        // Mock the behavior when an invalid URL is passed
//        assertThrows(IOException.class, () -> {
//            paintingServiceUnderTest.handleInvert(invalidUrl);
//        });
//    }
//
//    @Test
//    public void testHandleInvertNullImage() throws IOException {
//        // Arrange
//        String imageUrl = "http://example.com/null_image.jpg";
//
//        // Simulate a null image being returned by ImageIO.read
//        when(ImageIO.read(new URL(imageUrl))).thenReturn(null);
//
//        // Act and Assert
//        assertThrows(NullPointerException.class, () -> {
//            paintingServiceUnderTest.handleInvert(imageUrl);
//        });
//    }
}
