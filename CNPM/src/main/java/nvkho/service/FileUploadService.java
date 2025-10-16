package nvkho.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
	private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    public String uploadImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            
            String filename = UUID.randomUUID().toString() + extension;
            
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            
            Path path = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
                String filename = imageUrl.substring("/uploads/".length());
                Path path = Paths.get(uploadDir + filename);
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi xóa: " + e.getMessage());
        }
    }
}