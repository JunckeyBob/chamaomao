package cmmteam.project.dto;

import cmmteam.project.entity.enums.AnimalGender;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import lombok.Data;
import java.util.List;

@Data
public class AnimalResponseDto {
    private Long animalId;
    private String name;
    private String species;
    private String breed;
    private String age;
    private AnimalGender gender;
    private String healthStatus;
    private AdoptionStatusAnimal adoptionStatus;
    private String characteristics;
    private Long managedByUserId;
    private List<AnimalPhotoDto> photos;
    private List<AnimalLocationResponseDto> locations;
}