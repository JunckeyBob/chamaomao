package cmmteam.project.dto;

import cmmteam.project.entity.enums.AnimalGender;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import lombok.Data;
import java.util.List;

@Data
public class AnimalResponseDto {
    private int animalId;
    private String name;
    private String species;
    private String breed;
    private String age;
    private AnimalGender gender;
    private String healthStatus;
    private String basicinfo;
    private String catsnality;
    private AdoptionStatusAnimal adoptionStatus;
    private int managedByUserId;
    private List<AnimalPhotoDto> photos;
    private List<AnimalLocationResponseDto> locations;
}