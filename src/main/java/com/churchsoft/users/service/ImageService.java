package com.churchsoft.users.service;

import com.churchsoft.users.entity.ImageFile;
import com.churchsoft.users.repo.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;

    public Long create(MultipartFile file) throws IOException {
        validate(file);

        ImageFile image = ImageFile.builder()
                .data(file.getBytes())
                .contentType(file.getContentType())
                .build();

        return repository.save(image).getId();
    }

    public ImageFile get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void update(Long id, MultipartFile file) throws IOException {
        validate(file);

        ImageFile image = get(id);
        image.setData(file.getBytes());
        image.setContentType(file.getContentType());

        repository.save(image);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Image not found");
        }
        repository.deleteById(id);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files allowed");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Max image size is 2MB");
        }
    }
}
