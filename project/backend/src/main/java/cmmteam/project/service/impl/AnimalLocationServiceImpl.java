package cmmteam.project.service.impl;

import cmmteam.project.dto.AnimalLocationRequestDto;
import cmmteam.project.dto.AnimalLocationResponseDto;
import cmmteam.project.entity.Animal;
import cmmteam.project.entity.AnimalLocation;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.AnimalLocationRepository;
import cmmteam.project.repository.AnimalRepository;
import cmmteam.project.repository.UserRepository;
import cmmteam.project.service.AnimalLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalLocationServiceImpl implements AnimalLocationService {

    private final AnimalLocationRepository animalLocationRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnimalLocationServiceImpl(AnimalLocationRepository animalLocationRepository,
                                     AnimalRepository animalRepository,
                                     UserRepository userRepository) {
        this.animalLocationRepository = animalLocationRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    private AnimalLocationResponseDto convertToDto(AnimalLocation location) {
        if (location == null) return null;
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
    public AnimalLocationResponseDto addLocationForAnimal(Integer animalId,
                                                          AnimalLocationRequestDto locationRequestDto,
                                                          Integer reporterId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("动物未找到, ID: " + animalId + "，无法为其添加位置。"));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("位置报告用户未找到, ID: " + reporterId));

        AnimalLocation newLocation = new AnimalLocation();
        newLocation.setAnimal(animal);
        newLocation.setLatitude(locationRequestDto.getLatitude());
        newLocation.setLongitude(locationRequestDto.getLongitude());
        newLocation.setReportedByUser(reporter);

        AnimalLocation savedLocation = animalLocationRepository.save(newLocation);

        animal.getLocations().add(savedLocation);
        animalRepository.save(animal);

        return convertToDto(savedLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalLocationResponseDto> getLocationsByAnimalId(Integer animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new RuntimeException("动物未找到, ID: " + animalId + "，无法获取其位置记录。");
        }

        Animal animal = animalRepository.findById(animalId).orElseThrow(() -> new RuntimeException("逻辑错误：动物应存在但未找到"));
        return animal.getLocations().stream()
                .sorted((loc1, loc2) -> loc2.getTimestamp().compareTo(loc1.getTimestamp()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalLocationResponseDto> getLatestLocations(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("获取最新位置的限制数量必须大于0。");
        }
        // Pageable with #limit locations
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<AnimalLocation> latestLocations = animalLocationRepository.findAllByOrderByTimestampDesc(pageable);

        return latestLocations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLocation(Integer locationId, Integer adminId) {
        userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN) // 假设 UserRole 枚举中有 ADMIN
                .orElseThrow(() -> new SecurityException("只有管理员才能删除位置记录。用户ID: " + adminId));

        AnimalLocation location = animalLocationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("要删除的位置记录未找到, ID: " + locationId));

        animalLocationRepository.delete(location);

        System.out.println("位置记录 ID: " + locationId + " 已被管理员 ID: " + adminId + " 删除。");
    }
}