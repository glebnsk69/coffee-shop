package com.example.coffeeshop.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class ImageStorageService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final Path imageDirectory = Path.of("data", "images");
    private String getFileExtension(String format) {
        return "." + format.toLowerCase();
    }
    public ImageStorageService() throws IOException {
        Files.createDirectories(imageDirectory);
    }

    public String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }


    public byte[] loadImage(String fileName) throws IOException {
        Path filePath = imageDirectory.resolve(fileName).normalize();

        if (!filePath.startsWith(imageDirectory.normalize())) {
            throw new IllegalArgumentException("Invalid image path");
        }

        if (!Files.exists(filePath)) {
            throw new IOException("Image file not found: " + fileName);
        }

        return Files.readAllBytes(filePath);
    }

    public String saveImage(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image must not be empty");
        }

        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image size must not exceed 5 MB");
        }
        String format = validateImage(imageBytes);
        String fileName = UUID.randomUUID() + getFileExtension(format);

        Path filePath = imageDirectory.resolve(fileName);

        Files.write(
                filePath,
                imageBytes,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        );

        return fileName;
    }
    private String validateImage(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes)) {
            var imageInput = ImageIO.createImageInputStream(input);

            if (imageInput == null) {
                throw new IllegalArgumentException("Invalid image");
            }

            try (imageInput) {
                var readers = ImageIO.getImageReaders(imageInput);

                if (!readers.hasNext()) {
                    throw new IllegalArgumentException("Unsupported or invalid image format");
                }
                var reader = readers.next();
                try {
                    String format = reader.getFormatName();
                    reader.setInput(imageInput);
                    if (reader.getWidth(0) != 300 || reader.getHeight(0) != 300) {
                        throw new IllegalArgumentException(
                                "Image dimensions must be exactly 300x300 pixels"
                        );
                    }
                    return format;
                } finally {
                    reader.dispose();
                }
            }
        }
    }

}
