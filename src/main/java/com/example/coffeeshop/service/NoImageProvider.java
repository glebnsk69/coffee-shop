package com.example.coffeeshop.service;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class NoImageProvider {
    private final BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
    private final byte[] imageBytes;

    public NoImageProvider() {
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, 300, 300);

        graphics.setColor(Color.BLACK);
        graphics.drawString("Image not available", 85, 150);

        graphics.dispose();

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            imageBytes = output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate placeholder image", e);
        }



    }
    public byte[] getImageBytes() {
        return imageBytes.clone();
    }


}
