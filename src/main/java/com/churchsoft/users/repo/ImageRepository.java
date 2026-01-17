package com.churchsoft.users.repo;

import com.churchsoft.users.entity.ImageFile;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageFile, Long> {
}
