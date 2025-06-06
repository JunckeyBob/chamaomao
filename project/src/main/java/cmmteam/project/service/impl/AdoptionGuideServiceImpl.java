package cmmteam.project.service.impl;

import cmmteam.project.dto.AdoptionGuideRequestDto;
import cmmteam.project.dto.AdoptionGuideResponseDto;
import cmmteam.project.entity.AdoptionGuide;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.AdoptionGuideRepository;
import cmmteam.project.repository.UserRepository;
import cmmteam.project.service.AdoptionGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionGuideServiceImpl implements AdoptionGuideService {

    private final AdoptionGuideRepository guideRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdoptionGuideServiceImpl(AdoptionGuideRepository guideRepository, UserRepository userRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
    }

    private AdoptionGuideResponseDto convertToDto(AdoptionGuide guide) {
        if (guide == null) return null;
        AdoptionGuideResponseDto dto = new AdoptionGuideResponseDto();
        dto.setGuideId(guide.getGuideId());
        dto.setTitle(guide.getTitle());
        dto.setContent(guide.getContent());
        if (guide.getAuthorUser() != null) {
            dto.setAuthorId(guide.getAuthorUser().getId());
        }
        dto.setPublishDate(guide.getPublishDate());
        return dto;
    }

    @Override
    @Transactional
    public AdoptionGuideResponseDto createGuide(AdoptionGuideRequestDto guideRequestDto, Integer authorId) {
        User author = userRepository.findById(authorId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new SecurityException("只有管理员才能创建领养指南，或用户未找到 ID: " + authorId));

        AdoptionGuide guide = new AdoptionGuide();
        guide.setTitle(guideRequestDto.getTitle());
        guide.setContent(guideRequestDto.getContent());
        guide.setAuthorUser(author);

        AdoptionGuide savedGuide = guideRepository.save(guide);
        return convertToDto(savedGuide);
    }

    @Override
    @Transactional(readOnly = true)
    public AdoptionGuideResponseDto getGuideById(Integer guideId) {
        AdoptionGuide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("领养指南未找到, ID: " + guideId));
        return convertToDto(guide);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionGuideResponseDto> getAllGuides() {
        return guideRepository.findAllByOrderByPublishDateDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdoptionGuideResponseDto updateGuide(Integer guideId, AdoptionGuideRequestDto guideRequestDto, Integer editorId) {
        userRepository.findById(editorId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new SecurityException("只有管理员才能更新领养指南，或用户未找到 ID: " + editorId));

        AdoptionGuide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("待更新的领养指南未找到, ID: " + guideId));

        guide.setTitle(guideRequestDto.getTitle());
        guide.setContent(guideRequestDto.getContent());

        AdoptionGuide updatedGuide = guideRepository.save(guide);
        return convertToDto(updatedGuide);
    }

    @Override
    @Transactional
    public void deleteGuide(Integer guideId, Integer adminId) {
        userRepository.findById(adminId)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new SecurityException("只有管理员才能删除领养指南，或用户未找到 ID: " + adminId));

        if (!guideRepository.existsById(guideId)) {
            throw new RuntimeException("待删除的领养指南未找到, ID: " + guideId);
        }
        guideRepository.deleteById(guideId);
        System.out.println("领养指南 ID: " + guideId + " 已被管理员 ID: " + adminId + " 删除。");
    }
}