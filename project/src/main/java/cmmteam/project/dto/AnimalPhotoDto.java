package cmmteam.project.dto;

import lombok.Data;

@Data
public class AnimalPhotoDto {
    private Long photoId;
    private String photoUrl;
    private Long animalId;
}