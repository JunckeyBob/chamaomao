package cmmteam.project.service.impl;

import cmmteam.project.dto.*;
import cmmteam.project.entity.*;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.*;
import cmmteam.project.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnimalServiceImpl(AnimalRepository animalRepository,
            UserRepository userRepository) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    private AnimalResponseDto convertToDto(Animal animal) {
        if (animal == null)
            return null;
        AnimalResponseDto dto = new AnimalResponseDto();
        dto.setAnimalId(animal.getAnimalId());
        dto.setName(animal.getName());
        dto.setSpecies(animal.getSpecies());
        dto.setBreed(animal.getBreed());
        dto.setAge(animal.getAge());
        dto.setGender(animal.getGender());
        dto.setHealthStatus(animal.getHealthStatus());
        dto.setAdoptionStatus(animal.getAdoptionStatus());
        dto.setBasicinfo(animal.getBasicinfo());
        dto.setCatsnality(animal.getCatsnality());

        if (animal.getManagedByUser() != null) {
            dto.setManagedByUserId(animal.getManagedByUser().getId());
        }

        if (animal.getPhotos() != null) {
            dto.setPhotos(animal.getPhotos().stream().map(this::convertPhotoToDto).collect(Collectors.toList()));
        } else {
            dto.setPhotos(new ArrayList<>());
        }

        if (animal.getLocations() != null) {
            dto.setLocations(
                    animal.getLocations().stream().map(this::convertLocationToDto).collect(Collectors.toList()));
        } else {
            dto.setLocations(new ArrayList<>());
        }
        return dto;
    }

    private AnimalPhotoDto convertPhotoToDto(AnimalPhoto photo) {
        if (photo == null)
            return null;
        AnimalPhotoDto dto = new AnimalPhotoDto();
        dto.setPhotoId(photo.getPhotoId());
        dto.setPhotoUrl(photo.getPhotoUrl());
        if (photo.getAnimal() != null) {
            dto.setAnimalId(photo.getAnimal().getAnimalId());
        }
        return dto;
    }

    private AnimalLocationResponseDto convertLocationToDto(AnimalLocation location) {
        if (location == null)
            return null;
        AnimalLocationResponseDto dto = new AnimalLocationResponseDto();
        dto.setLocationId(location.getLocationId());
        if (location.getAnimal() != null) {
            dto.setAnimalId(location.getAnimal().getAnimalId());
        }
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setTimestamp(location.getTimestamp());
        if (location.getReportedByUser() != null) {
            dto.setReportedByUserId(location.getReportedByUser().getId());
        }
        return dto;
    }

    @Override
    @Transactional
    public AnimalResponseDto createAnimal(AnimalRequestDto animalRequestDto, Integer submitterId) {
        User submitter = userRepository.findById(submitterId)
                .orElseThrow(() -> new RuntimeException("提交动物信息的用户未找到, ID: " + submitterId));

        Animal animal = new Animal();
        animal.setName(animalRequestDto.getName());
        animal.setSpecies(animalRequestDto.getSpecies());
        animal.setBreed(animalRequestDto.getBreed());
        animal.setAge(animalRequestDto.getAge());
        animal.setGender(animalRequestDto.getGender());
        animal.setHealthStatus(animalRequestDto.getHealthStatus());
        animal.setCatsnality(animalRequestDto.getCatsnality());
        animal.setBasicinfo(animalRequestDto.getBasicinfo());

        animal.setManagedByUser(submitter);

        // new animal is from admin
        if (submitter.getRole() == UserRole.ADMIN) {
            animal.setAdoptionStatus(animalRequestDto.getAdoptionStatus() != null ? animalRequestDto.getAdoptionStatus()
                    : AdoptionStatusAnimal.AVAILABLE);
        }
        // from normal user
        else {
            animal.setAdoptionStatus(AdoptionStatusAnimal.AVAILABLE);
        }

        animal.setPhotos(new ArrayList<>());
        animal.setLocations(new ArrayList<>());

        Animal savedAnimal = animalRepository.save(animal);

        if (animalRequestDto.getPhotoUrls() != null && !animalRequestDto.getPhotoUrls().isEmpty()) {
            for (String photoUrl : animalRequestDto.getPhotoUrls()) {
                AnimalPhoto animalPhoto = new AnimalPhoto();
                animalPhoto.setAnimal(savedAnimal);
                animalPhoto.setPhotoUrl(photoUrl);
                savedAnimal.getPhotos().add(animalPhoto);
            }
        }

        if (animalRequestDto.getInitialLocation() != null) {
            AnimalLocationRequestDto locDto = animalRequestDto.getInitialLocation();
            AnimalLocation location = new AnimalLocation();
            location.setAnimal(savedAnimal);
            location.setLatitude(locDto.getLatitude());
            location.setLongitude(locDto.getLongitude());
            location.setReportedByUser(submitter);
            savedAnimal.getLocations().add(location);
        }

        Animal finalSavedAnimal = animalRepository.save(savedAnimal);

        return convertToDto(finalSavedAnimal);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalResponseDto getAnimalById(Integer animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("动物信息未找到, ID: " + animalId));
        return convertToDto(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalResponseDto> getAllAnimalsForAdoption() {
        // show all animal for now
        return animalRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnimalResponseDto updateAnimal(Integer animalId, AnimalRequestDto animalRequestDto, Integer adminId) {
        userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new RuntimeException("操作用户不是管理员或未找到, ID: " + adminId));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("待更新的动物信息未找到, ID: " + animalId));

        animal.setName(animalRequestDto.getName());
        animal.setSpecies(animalRequestDto.getSpecies());
        animal.setBreed(animalRequestDto.getBreed());
        animal.setAge(animalRequestDto.getAge());
        animal.setGender(animalRequestDto.getGender());
        animal.setHealthStatus(animalRequestDto.getHealthStatus());

        if (animalRequestDto.getAdoptionStatus() != null) {
            animal.setAdoptionStatus(animalRequestDto.getAdoptionStatus());
        }

        Animal updatedAnimal = animalRepository.save(animal);
        return convertToDto(updatedAnimal);
    }

    @Override
    @Transactional
    public void deleteAnimal(Integer animalId, Integer adminId) {
        userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new RuntimeException("操作用户不是管理员或未找到, ID: " + adminId));

        if (!animalRepository.existsById(animalId)) {
            throw new RuntimeException("待删除的动物信息未找到, ID: " + animalId);
        }
        animalRepository.deleteById(animalId);
    }

    @Override
    @Transactional
    public AnimalResponseDto reviewAnimal(Integer animalId, boolean approved, Integer adminId) {
        userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new RuntimeException("操作用户不是管理员或未找到, ID: " + adminId));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("待审核的动物信息未找到, ID: " + animalId));

        if (animal.getAdoptionStatus() != AdoptionStatusAnimal.INFO_PENDING_REVIEW) {
            throw new IllegalStateException("该动物信息不处于待审核状态。当前状态: " + animal.getAdoptionStatus());
        }

        if (approved) {
            animal.setAdoptionStatus(AdoptionStatusAnimal.AVAILABLE);
        } else {
            animalRepository.delete(animal);
            return null;
        }

        Animal reviewedAnimal = animalRepository.save(animal);
        return convertToDto(reviewedAnimal);
    }
}