package cmmteam.project.service;

import cmmteam.project.dto.AnimalRequestDto;
import cmmteam.project.dto.AnimalResponseDto;

import java.util.List;

public interface AnimalService {

    AnimalResponseDto createAnimal(AnimalRequestDto animalRequestDto, Integer submitterId);

    AnimalResponseDto getAnimalById(Integer animalId);

    List<AnimalResponseDto> getAllAnimalsForAdoption();

    AnimalResponseDto updateAnimal(Integer animalId, AnimalRequestDto animalRequestDto, Integer adminId);

    void deleteAnimal(Integer animalId, Integer adminId);

    AnimalResponseDto reviewAnimal(Integer animalId, boolean approved, Integer adminId);

}
