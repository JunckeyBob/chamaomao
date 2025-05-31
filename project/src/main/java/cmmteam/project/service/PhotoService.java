package cmmteam.project.service;

import cmmteam.project.dto.AnimalPhotoDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PhotoService {
    AnimalPhotoDto uploadPhoto(Integer animalId, MultipartFile file, Authentication authentication);
    List<AnimalPhotoDto> getPhotosByAnimalId(Integer animalId);
    void deletePhoto(Integer animalId, Integer photoId, Authentication authentication);
}