package cmmteam.project.controller;

import cmmteam.project.dto.AnimalLocationRequestDto;
import cmmteam.project.dto.AnimalLocationResponseDto;
import cmmteam.project.entity.User;
import cmmteam.project.service.AnimalLocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // 基础路径
public class AnimalLocationController {

    private final AnimalLocationService animalLocationService;

    @Autowired
    public AnimalLocationController(AnimalLocationService animalLocationService) {
        this.animalLocationService = animalLocationService;
    }

    private Integer getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("用户未认证。");
        }
        return ((User) authentication.getPrincipal()).getId();
    }

    @PostMapping("/animals/{animalId}/locations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addLocationForAnimal(
            @PathVariable Integer animalId,
            @Valid @RequestBody AnimalLocationRequestDto locationRequestDto,
            Authentication authentication) {
        try {
            Integer reporterId = getCurrentUserId(authentication);
            AnimalLocationResponseDto responseDto = animalLocationService.addLocationForAnimal(animalId, locationRequestDto, reporterId);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error adding location for animal " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加动物位置时发生错误。");
        }
    }

    @GetMapping("/animals/{animalId}/locations")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getLocationsByAnimalId(@PathVariable Integer animalId) {
        try {
            List<AnimalLocationResponseDto> locations = animalLocationService.getLocationsByAnimalId(animalId);
            if (locations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到动物ID " + animalId + " 的位置记录。");
            }
            return ResponseEntity.ok(locations);
        } catch (RuntimeException e) { // 例如 AnimalNotFoundException from service
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Error getting locations for animal " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/locations/latest")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AnimalLocationResponseDto>> getLatestLocations(
            @RequestParam(defaultValue = "10") int limit) { //10 locations for default
        try {
            if (limit <= 0 || limit > 100) {
                return ResponseEntity.badRequest().body(null);
            }
            List<AnimalLocationResponseDto> latestLocations = animalLocationService.getLatestLocations(limit);
            return ResponseEntity.ok(latestLocations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.err.println("Error getting latest locations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/locations/{locationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteLocation(@PathVariable Integer locationId,
                                                 Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            animalLocationService.deleteLocation(locationId, adminId);
            return ResponseEntity.ok("位置记录 ID: " + locationId + " 已成功删除。");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting location " + locationId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除位置记录时发生错误。");
        }
    }
}