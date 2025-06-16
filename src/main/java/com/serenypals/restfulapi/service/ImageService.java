package com.serenypals.restfulapi.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.serenypals.restfulapi.util.PasswordHasherMatcher;
import com.serenypals.restfulapi.util.Base64Converter;

@Service
public class ImageService {

    @Autowired
    private PasswordHasherMatcher passwordHasherMatcher;

    @Autowired
    private Base64Converter base64Converter;

    @Value("${storage.server-host}${storage.api-prefix}/")
    private String serverPath;

    public String saveImage(String fileName, MultipartFile file, String pathToFoto, boolean fitPicture, Boolean isCompressing) {
        try {
            fileName = base64Converter.encrypt(passwordHasherMatcher.hashPassword(fileName));
            Path uploadPath = Paths.get(pathToFoto);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save as JPEG, because we're too alpha for PNG
            Path filePath = uploadPath.resolve(fileName + ".jpeg");

            // Read the input image (mew, make sure it's legit)
            BufferedImage inputImage = ImageIO.read(file.getInputStream());
            if (inputImage == null) {
                return "";
            }
            saveFile(fileName, inputImage, pathToFoto, fitPicture, filePath, isCompressing);
            return pathToFoto + fileName + ".jpeg";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Async
    public CompletableFuture<Void> saveFile(String fileName, BufferedImage inputImage, String pathToFoto,
            boolean fitPicture, Path filePath, boolean isCompressing) {
    try{
        BufferedImage squaredImage = new BufferedImage(
                inputImage.getWidth(),
                inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = squaredImage.createGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();
        if (fitPicture) {
            // Calculate dimensions and create a square canvas (1:1 scale, pure gigachad)
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            int maxDim = Math.max(width, height);
            squaredImage = new BufferedImage(maxDim, maxDim, BufferedImage.TYPE_INT_RGB);

            // Draw white background for that dope border effect
            Graphics2D g2d = squaredImage.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, maxDim, maxDim);

            // Center the original image on the canvas (true rizz move)
            int x = (maxDim - width) / 2;
            int y = (maxDim - height) / 2;
            g2d.drawImage(inputImage, x, y, null);
            g2d.dispose();
        }

        // Optional: delete previous image if needed, cuz we don't keep beta leftovers
        deleteImage(pathToFoto + fileName + ".jpeg");

        // Compress the image if its size is above 3KB, iteratively lowering quality
        // (edge compression!)
        float quality = 1.0f;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes;

        while (true) {
            baos.reset();
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(quality);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                jpgWriter.setOutput(ios);
                IIOImage outputImage = new IIOImage(squaredImage, null, null);
                jpgWriter.write(null, outputImage, jpgWriteParam);
            }
            jpgWriter.dispose();
            imageBytes = baos.toByteArray();

            // Break if the image is under 3KB or quality drops too low (no beta quality
            // allowed)
            if (imageBytes.length <= 300_000 || !isCompressing) {
                break;
            }
            quality -= 0.05f; // Reduce quality by 5%, skibidi style!
        }

        // Write the final JPEG image to disk, straight up, no cap.
        Files.write(filePath, imageBytes);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return CompletableFuture.completedFuture(null);
}

    public boolean deleteImage(String fileName) {
        Path imagePath = Paths.get(fileName).normalize();
        try {
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getImage(String imagePath) {
        Path filePath = Paths.get(imagePath);
        if (Files.exists(filePath) && !imagePath.equals("")) {
            String returnPath = filePath.toString();
            String encodedPath = Base64.getEncoder().encodeToString(returnPath.getBytes());
            return serverPath + "images/" + encodedPath;
        }
        return "";
    }
}
