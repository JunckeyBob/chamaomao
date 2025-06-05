package cmmteam.project.service;

import cmmteam.project.dto.AdoptionApplicationRequestDto;
import cmmteam.project.dto.AdoptionApplicationResponseDto;
import cmmteam.project.dto.AdoptionApplicationReviewDto;

import java.util.List;

public interface AdoptionApplicationService {

    AdoptionApplicationResponseDto submitApplication(AdoptionApplicationRequestDto requestDto, Integer applicantUserId);

    AdoptionApplicationResponseDto reviewApplication(Integer applicationId, AdoptionApplicationReviewDto reviewDto, Integer adminId);

    AdoptionApplicationResponseDto cancelApplication(Integer applicationId, Integer applicantUserId);

    AdoptionApplicationResponseDto getApplicationById(Integer applicationId, Integer currentUserId);

    List<AdoptionApplicationResponseDto> getApplicationsByApplicant(Integer applicantUserId);

    List<AdoptionApplicationResponseDto> getAllApplicationsForAdmin(String status);
}