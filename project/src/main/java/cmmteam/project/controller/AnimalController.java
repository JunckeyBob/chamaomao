package cmmteam.project.controller;

import cmmteam.project.dto.AnimalRequestDto;
import cmmteam.project.dto.AnimalResponseDto;
import cmmteam.project.entity.User;
import cmmteam.project.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    private Integer getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未认证。");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            throw new IllegalStateException("认证主体不是预期的用户类型。");
        }
        return ((User) principal).getId();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createAnimal(@Valid @RequestBody AnimalRequestDto animalRequestDto,
                                          Authentication authentication) {
        try {
            Integer submitterId = getCurrentUserId(authentication);
            AnimalResponseDto createdAnimal = animalService.createAnimal(animalRequestDto, submitterId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAnimal);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating animal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("创建动物信息时发生错误。");
        }
    }

    @GetMapping("/{animalId}")
    public ResponseEntity<?> getAnimalById(@PathVariable Integer animalId) {
        try {
            AnimalResponseDto animal = animalService.getAnimalById(animalId);
            return ResponseEntity.ok(animal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error getting animal by ID " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取动物信息时发生错误。");
        }
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDto>> getAllAnimals() {
        try {
            List<AnimalResponseDto> animals = animalService.getAllAnimalsForAdoption();
            return ResponseEntity.ok(animals);
        } catch (Exception e) {
            System.err.println("Error getting all animals: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{animalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAnimal(@PathVariable Integer animalId,
                                          @Valid @RequestBody AnimalRequestDto animalRequestDto,
                                          Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            AnimalResponseDto updatedAnimal = animalService.updateAnimal(animalId, animalRequestDto, adminId);
            return ResponseEntity.ok(updatedAnimal);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating animal " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新动物信息时发生错误。");
        }
    }

    @DeleteMapping("/{animalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAnimal(@PathVariable Integer animalId,
                                               Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            animalService.deleteAnimal(animalId, adminId);
            return ResponseEntity.ok("动物信息删除成功, ID: " + animalId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting animal " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除动物信息时发生错误。");
        }
    }

    @PutMapping("/{animalId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reviewAnimal(@PathVariable Integer animalId,
                                          @RequestParam boolean approved,
                                          Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            AnimalResponseDto reviewedAnimal = animalService.reviewAnimal(animalId, approved, adminId);

            if (!approved && reviewedAnimal == null) {
                return ResponseEntity.ok("动物信息审核未通过并已成功删除, ID: " + animalId);
            } else if (approved && reviewedAnimal != null) {
                return ResponseEntity.ok(reviewedAnimal);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("审核操作后返回状态不一致。");
            }

        } catch (IllegalStateException e) {
            if (e.getMessage().contains("用户未认证") || e.getMessage().contains("认证主体不是预期的用户类型")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error reviewing animal " + animalId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("审核动物信息时发生错误。");
        }
    }
}