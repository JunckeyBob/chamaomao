package cmmteam.project.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnimalLocationResponseDto {
    private Long locationId;
    private Long animalId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private LocalDateTime timestamp;
    private Long reportedByUserId;
}