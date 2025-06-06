package cmmteam.project.service.impl;

import cmmteam.project.dto.AdoptionApplicationRequestDto;
import cmmteam.project.dto.AdoptionApplicationResponseDto;
import cmmteam.project.dto.AdoptionApplicationReviewDto;
import cmmteam.project.entity.AdoptionApplication;
import cmmteam.project.entity.Animal;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import cmmteam.project.entity.enums.AdoptionStatusApplication;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.AdoptionApplicationRepository;
import cmmteam.project.repository.AnimalRepository;
import cmmteam.project.repository.UserRepository;
import cmmteam.project.service.AdoptionApplicationService;
import cmmteam.project.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionApplicationServiceImpl implements AdoptionApplicationService {

    private final AdoptionApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final AnimalService animalService;

    @Autowired
    public AdoptionApplicationServiceImpl(AdoptionApplicationRepository applicationRepository,
                                          UserRepository userRepository,
                                          AnimalRepository animalRepository,
                                          AnimalService animalService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
        this.animalService = animalService;
    }

    // --- DTO 与 Entity 转换辅助方法 ---
    private AdoptionApplicationResponseDto convertToDto(AdoptionApplication application) {
        if (application == null) return null;
        AdoptionApplicationResponseDto dto = new AdoptionApplicationResponseDto();
        dto.setAdoptionId(application.getAdoptionId());
        dto.setAnimalId(application.getAnimal().getAnimalId());
        dto.setApplicantUserId(application.getApplicantUser().getId());
        dto.setStatus(application.getStatus());
        dto.setDetails(application.getDetails());
        dto.setReviewDetails(application.getReviewDetails());
        if (application.getReviewedByUser() != null) {
            dto.setReviewedByUserId(application.getReviewedByUser().getId());
        }
        dto.setApplicationDate(application.getApplicationDate());
        dto.setReviewDate(application.getReviewDate());
        return dto;
    }

    @Override
    @Transactional
    public AdoptionApplicationResponseDto submitApplication(AdoptionApplicationRequestDto requestDto, Integer applicantUserId) {
        User applicant = userRepository.findById(applicantUserId)
                .orElseThrow(() -> new RuntimeException("申请用户未找到, ID: " + applicantUserId));

        Animal animal = animalRepository.findById(requestDto.getAnimalId())
                .orElseThrow(() -> new RuntimeException("申请的动物未找到, ID: " + requestDto.getAnimalId()));

        // CHECK: Is animal available?
        if (animal.getAdoptionStatus() != AdoptionStatusAnimal.AVAILABLE) {
            throw new IllegalStateException("该动物当前不可领养，状态为: " + animal.getAdoptionStatus());
        }

        // CHECK: Duplicate application?
        applicationRepository.findByApplicantUserAndAnimalAndStatus(
                        applicant, animal, AdoptionStatusApplication.PENDING_REVIEW)
                .ifPresent(app -> {
                    throw new IllegalStateException("您已对该动物提交过领养申请，请勿重复提交。");
                });

        AdoptionApplication application = new AdoptionApplication();
        application.setApplicantUser(applicant);
        application.setAnimal(animal);
        application.setDetails(requestDto.getDetails());
        application.setStatus(AdoptionStatusApplication.PENDING_REVIEW);

        AdoptionApplication savedApplication = applicationRepository.save(application);

        animal.setAdoptionStatus(AdoptionStatusAnimal.PENDING_ADOPTION);
        animalRepository.save(animal);

        return convertToDto(savedApplication);
    }

    @Override
    @Transactional
    public AdoptionApplicationResponseDto reviewApplication(Integer applicationId, AdoptionApplicationReviewDto reviewDto, Integer adminId) {
        User admin = userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new RuntimeException("操作用户不是管理员或未找到, ID: " + adminId));

        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("领养申请未找到, ID: " + applicationId));

        if (application.getStatus() != AdoptionStatusApplication.PENDING_REVIEW) {
            throw new IllegalStateException("该领养申请不处于待审核状态，无法操作。当前状态: " + application.getStatus());
        }

        application.setStatus(reviewDto.getStatus()); // APPROVED or REJECTED
        application.setReviewDetails(reviewDto.getReviewDetails());
        application.setReviewedByUser(admin);

        AdoptionApplication updatedApplication = applicationRepository.save(application);

        if (updatedApplication.getStatus() == AdoptionStatusApplication.APPROVED) {
            Animal adoptedAnimal = updatedApplication.getAnimal();
            if (adoptedAnimal.getAdoptionStatus() == AdoptionStatusAnimal.AVAILABLE ||
                    adoptedAnimal.getAdoptionStatus() == AdoptionStatusAnimal.PENDING_ADOPTION) {
                adoptedAnimal.setAdoptionStatus(AdoptionStatusAnimal.ADOPTED);
                animalRepository.save(adoptedAnimal);

                // Reject all other applications
                List<AdoptionApplication> otherPendingApplications = applicationRepository
                        .findByAnimalAndStatus(adoptedAnimal, AdoptionStatusApplication.PENDING_REVIEW);
                for (AdoptionApplication otherApp : otherPendingApplications) {
                    if (!otherApp.getAdoptionId().equals(updatedApplication.getAdoptionId())) {
                        otherApp.setStatus(AdoptionStatusApplication.REJECTED);
                        otherApp.setReviewDetails("该动物已被其他申请人成功领养。");
                        otherApp.setReviewedByUser(admin);
                        applicationRepository.save(otherApp);
                    }
                }
            } else {
                // Wrong status when approved. Roll back?
                System.err.println("警告：领养申请 " + applicationId + " 被批准，但动物 " +
                        adoptedAnimal.getAnimalId() + " 当前状态为 " + adoptedAnimal.getAdoptionStatus() +
                        "，未自动更新为ADOPTED。");
            }
        } else if (updatedApplication.getStatus() == AdoptionStatusApplication.REJECTED) {
            Animal animal = updatedApplication.getAnimal();
            if (animal.getAdoptionStatus() == AdoptionStatusAnimal.PENDING_ADOPTION) {
                animal.setAdoptionStatus(AdoptionStatusAnimal.AVAILABLE);
                animalRepository.save(animal);
            }
        }
        return convertToDto(updatedApplication);
    }

    @Override
    @Transactional
    public AdoptionApplicationResponseDto cancelApplication(Integer applicationId, Integer applicantUserId) {
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("领养申请未找到, ID: " + applicationId));

        if (!application.getApplicantUser().getId().equals(applicantUserId)) {
            throw new SecurityException("无权取消他人的领养申请。"); // 或者返回403
        }

        if (application.getStatus() != AdoptionStatusApplication.PENDING_REVIEW) {
            throw new IllegalStateException("该领养申请当前状态为 " + application.getStatus() + "，无法取消。");
        }

        application.setStatus(AdoptionStatusApplication.CANCELLED);

        AdoptionApplication cancelledApplication = applicationRepository.save(application);

        Animal animal = cancelledApplication.getAnimal();
        if (animal.getAdoptionStatus() == AdoptionStatusAnimal.PENDING_ADOPTION) {
            boolean hasOtherPending = applicationRepository
                    .findByAnimalAndStatus(animal, AdoptionStatusApplication.PENDING_REVIEW)
                    .stream()
                    .anyMatch(app -> !app.getAdoptionId().equals(cancelledApplication.getAdoptionId()));
            if (!hasOtherPending) {
                animal.setAdoptionStatus(AdoptionStatusAnimal.AVAILABLE);
                animalRepository.save(animal);
            }
        }

        return convertToDto(cancelledApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public AdoptionApplicationResponseDto getApplicationById(Integer applicationId, Integer currentUserId) {
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("领养申请未找到, ID: " + applicationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("当前用户未找到, ID: " + currentUserId));

        if (currentUser.getRole() != UserRole.ADMIN && !application.getApplicantUser().getId().equals(currentUserId)) {
            throw new SecurityException("无权查看他人的领养申请详情。");
        }
        return convertToDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionApplicationResponseDto> getApplicationsByApplicant(Integer applicantUserId) {
        return applicationRepository.findByApplicantUser_Id(applicantUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionApplicationResponseDto> getAllApplicationsForAdmin(String statusString) {
        List<AdoptionApplication> applications;
        if (statusString != null && !statusString.trim().isEmpty()) {
            try {
                AdoptionStatusApplication status = AdoptionStatusApplication.valueOf(statusString.toUpperCase());
                applications = applicationRepository.findByStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的申请状态值: " + statusString);
            }
        } else {
            applications = applicationRepository.findAll();
        }
        return applications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}