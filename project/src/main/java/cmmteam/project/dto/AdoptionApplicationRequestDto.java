package cmmteam.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdoptionApplicationRequestDto {
    @NotNull(message = "申请领养的动物ID不能为空。")
    private int animalId;

    @Size(max = 1000, message = "申请详情不能超过1000个字符。")
    private String details;
}