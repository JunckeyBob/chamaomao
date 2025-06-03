package cmmteam.project.dto;

import cmmteam.project.entity.enums.AdoptionStatusApplication;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionApplicationResponseDto {
    private int adoptionId;
    private int animalId;
    private int applicantUserId;
    private AdoptionStatusApplication status;
    private String details;
    private String reviewDetails;
    private int reviewedByUserId;
    private LocalDateTime applicationDate;
    private LocalDateTime reviewDate;
}