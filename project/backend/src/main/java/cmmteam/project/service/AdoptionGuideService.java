package cmmteam.project.service;

import cmmteam.project.dto.AdoptionGuideRequestDto;
import cmmteam.project.dto.AdoptionGuideResponseDto;

import java.util.List;

public interface AdoptionGuideService {

    AdoptionGuideResponseDto createGuide(AdoptionGuideRequestDto guideRequestDto, Integer authorId);
    
    AdoptionGuideResponseDto getGuideById(Integer guideId);

    List<AdoptionGuideResponseDto> getAllGuides();

    AdoptionGuideResponseDto updateGuide(Integer guideId, AdoptionGuideRequestDto guideRequestDto, Integer authorId);

    void deleteGuide(Integer guideId, Integer adminId);
}