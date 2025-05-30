package cmmteam.project.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnimalLocationResponseDto {
    private int locationId;
    private int animalId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private LocalDateTime timestamp;
    private int reportedByUserId;
}