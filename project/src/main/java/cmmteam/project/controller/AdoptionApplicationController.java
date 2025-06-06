package cmmteam.project.controller;

import cmmteam.project.dto.AdoptionApplicationRequestDto;
import cmmteam.project.dto.AdoptionApplicationResponseDto;
import cmmteam.project.dto.AdoptionApplicationReviewDto;
import cmmteam.project.entity.User;
import cmmteam.project.service.AdoptionApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-applications")
public class AdoptionApplicationController {

    private final AdoptionApplicationService applicationService;

    @Autowired
    public AdoptionApplicationController(AdoptionApplicationService applicationService) {
        this.applicationService = applicationService;
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
    public ResponseEntity<?> submitApplication(@Valid @RequestBody AdoptionApplicationRequestDto requestDto,
                                               Authentication authentication) {
        try {
            Integer applicantUserId = getCurrentUserId(authentication);
            AdoptionApplicationResponseDto responseDto = applicationService.submitApplication(requestDto, applicantUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error submitting adoption application: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("提交领养申请时发生错误。");
        }
    }

    @PutMapping("/{applicationId}/review")
    @PreAuthorize("hasRole('ADMIN')") // 只有管理员可以审核
    public ResponseEntity<?> reviewApplication(@PathVariable Integer applicationId,
                                               @Valid @RequestBody AdoptionApplicationReviewDto reviewDto,
                                               Authentication authentication) {
        try {
            Integer adminId = getCurrentUserId(authentication);
            AdoptionApplicationResponseDto responseDto = applicationService.reviewApplication(applicationId, reviewDto, adminId);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 例如：申请不处于待审核状态
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) { // 例如申请或管理员用户未找到
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error reviewing adoption application " + applicationId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("审核领养申请时发生错误。");
        }
    }

    @PutMapping("/{applicationId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelApplication(@PathVariable Integer applicationId,
                                               Authentication authentication) {
        try {
            Integer applicantUserId = getCurrentUserId(authentication);
            AdoptionApplicationResponseDto responseDto = applicationService.cancelApplication(applicationId, applicantUserId);
            return ResponseEntity.ok(responseDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error cancelling adoption application " + applicationId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("取消领养申请时发生错误。");
        }
    }


    @GetMapping("/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getApplicationById(@PathVariable Integer applicationId,
                                                Authentication authentication) {
        try {
            Integer currentUserId = getCurrentUserId(authentication);
            AdoptionApplicationResponseDto responseDto = applicationService.getApplicationById(applicationId, currentUserId);
            return ResponseEntity.ok(responseDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error getting adoption application by ID " + applicationId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取领养申请详情时发生错误。");
        }
    }

    @GetMapping("/my-applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AdoptionApplicationResponseDto>> getMyApplications(Authentication authentication) {
        try {
            Integer applicantUserId = getCurrentUserId(authentication);
            List<AdoptionApplicationResponseDto> applications = applicationService.getApplicationsByApplicant(applicantUserId);
            return ResponseEntity.ok(applications);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        catch (Exception e) {
            System.err.println("Error getting current user's applications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllApplicationsForAdmin(
            @RequestParam(required = false) String status) {
        try {
            List<AdoptionApplicationResponseDto> applications = applicationService.getAllApplicationsForAdmin(status);
            return ResponseEntity.ok(applications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error admin getting all applications: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}