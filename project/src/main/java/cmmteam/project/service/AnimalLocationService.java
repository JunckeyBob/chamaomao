package cmmteam.project.service;

import cmmteam.project.dto.AnimalLocationRequestDto;
import cmmteam.project.dto.AnimalLocationResponseDto;

import java.util.List;

public interface AnimalLocationService {

    AnimalLocationResponseDto addLocationForAnimal(Integer animalId, AnimalLocationRequestDto locationRequestDto, Integer reporterId);

    List<AnimalLocationResponseDto> getLocationsByAnimalId(Integer animalId);

    List<AnimalLocationResponseDto> getLatestLocations(int limit);

    void deleteLocation(Integer locationId, Integer adminId);

}