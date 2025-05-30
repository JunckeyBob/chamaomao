package cmmteam.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AnimalLocationRequestDto {
    private int animalId;

    @NotNull(message = "纬度不能为空。")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空。")
    private BigDecimal longitude;

    private String description;
}