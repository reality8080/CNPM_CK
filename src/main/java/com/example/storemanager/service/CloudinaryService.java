package com.example.storemanager.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String name,
            @Value("${cloudinary.api-key}") String key,
            @Value("${cloudinary.api-secret}") String secret) {
        if (name == null || name.isBlank() || key == null || key.isBlank() || secret == null || secret.isBlank()) {
            throw new IllegalStateException("Cloudinary config missing: check cloudinary.cloud-name/api-key/api-secret");
        }
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", name,
                "api_key", key,
                "api_secret", secret,
                "secure", true));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) upload.get("secure_url");
    }
}
