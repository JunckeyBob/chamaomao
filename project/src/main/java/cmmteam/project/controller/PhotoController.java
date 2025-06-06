package cmmteam.project.controller;

import cmmteam.project.service.PhotoService;
import cmmteam.project.dto.AnimalPhotoDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/animals/{animalId}/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    // Upload photo for an animal
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Integer animalId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请选择要上传的文件。");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || 
            (!contentType.equalsIgnoreCase("image/jpeg") && 
            !contentType.equalsIgnoreCase("image/png") &&
            !contentType.equalsIgnoreCase("image/gif"))) {
            return ResponseEntity.badRequest().body("只支持JPEG、PNG或GIF格式的图片。");
        }

        // Check file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("图片大小不能超过5MB。");
        }

        try {
            AnimalPhotoDto photo = photoService.uploadPhoto(animalId, file, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(photo);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传图片时发生错误。");
        }
    }

    // Get all photos for an animal
    @GetMapping
    public ResponseEntity<?> getPhotosByAnimalId(@PathVariable Integer animalId) { // List<AnimalPhoto> or error
        try {
            List<AnimalPhotoDto> photos = photoService.getPhotosByAnimalId(animalId);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Delete a photo
    @DeleteMapping("/{photoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePhoto(
            @PathVariable Integer animalId,
            @PathVariable Integer photoId,
            Authentication authentication) {
        
        try {
            photoService.deletePhoto(animalId, photoId, authentication);
            return ResponseEntity.ok("图片删除成功。");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除图片时发生错误。");
        }
    }
}