package demo.JPA.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PictureService {

    private final String UPLOAD_DIR = "uploads/";

    public String savePicture(MultipartFile file) throws IOException {
        String token = UUID.randomUUID().toString();
        String filename = token + ".jpg";
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.copy(file.getInputStream(), filePath);
        return token;
    }

    public Resource loadPicture(String token) {
        try {
            Path file = Paths.get(UPLOAD_DIR + token + ".jpg");
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) return resource;
            else throw new FileNotFoundException();
        } catch (Exception e) {
            throw new RuntimeException("사진을 불러올 수 없습니다.", e);
        }
    }
}
