package cmmteam.project.controller;

import cmmteam.project.dto.AdoptionGuideRequestDto;
import cmmteam.project.dto.AdoptionGuideResponseDto;
import cmmteam.project.entity.User;
import cmmteam.project.service.AdoptionGuideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-guides")
public class AdoptionGuideController {

    private final AdoptionGuideService guideService;

    @Autowired
    public AdoptionGuideController(AdoptionGuideService guideService) {
        this.guideService = guideService;
    }

    private Integer getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("用户未认证。");
        }
        return ((User) authentication.getPrincipal()).getId();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createGuide(@Valid @RequestBody AdoptionGuideRequestDto guideRequestDto,
                                         Authentication authentication) {
        try {
            Integer authorId = getCurrentUserId(authentication);
            AdoptionGuideResponseDto createdGuide = guideService.createGuide(guideRequestDto, authorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGuide);
        } catch (SecurityException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating adoption guide: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("创建领养指南时发生错误。");
        }
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdoptionGuideResponseDto>> getAllGuides() {
        try {
            List<AdoptionGuideResponseDto> guides = guideService.getAllGuides();
            return ResponseEntity.ok(guides);
        } catch (Exception e) {
            System.err.println("Error getting all adoption guides: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{guideId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getGuideById(@PathVariable Integer guideId) {
        try {
            AdoptionGuideResponseDto guide = guideService.getGuideById(guideId);
            return ResponseEntity.ok(guide);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error getting adoption guide by ID " + guideId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取领养指南详情时发生错误。");
        }
    }

    @PutMapping("/{guideId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGuide(@PathVariable Integer guideId,
                                         @Valid @RequestBody AdoptionGuideRequestDto guideRequestDto,
                                         Authentication authentication) {
        try {
            Integer editorId = getCurrentUserId(authentication);
            AdoptionGuideResponseDto updatedGuide = guideService.updateGuide(guideId, guideRequestDto, editorId);
            return ResponseEntity.ok(updatedGuide);
        } catch (SecurityException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating adoption guide " + guideId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新领养指南时发生错误。");
        }
    }

    @DeleteMapping("/{guideId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteGuide(@PathVariable Integer guideId,
                                              Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            guideService.deleteGuide(guideId, adminId);
            return ResponseEntity.ok("领养指南 ID: " + guideId + " 已成功删除。");
        } catch (SecurityException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting adoption guide " + guideId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除领养指南时发生错误。");
        }
    }
}