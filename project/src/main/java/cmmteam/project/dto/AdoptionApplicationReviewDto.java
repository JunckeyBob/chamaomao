package cmmteam.project.dto;

import cmmteam.project.entity.enums.AdoptionStatusApplication;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdoptionApplicationReviewDto {

    @NotNull(message = "审核状态不能为空。")
    private AdoptionStatusApplication status;

    @Size(max = 500, message = "审核备注不能超过500个字符。")
    private String reviewDetails;
}