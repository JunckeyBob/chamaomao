package cmmteam.project.dto;

import cmmteam.project.entity.enums.AdoptionStatusApplication;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionApplicationResponseDto {
    private Long adoptionId;
    private Long animalId;
    private Long applicantUserId;
    private AdoptionStatusApplication status;
    private String details;
    private String reviewDetails;
    private Long reviewedByUserId;
    private LocalDateTime applicationDate;
    private LocalDateTime reviewDate;
}