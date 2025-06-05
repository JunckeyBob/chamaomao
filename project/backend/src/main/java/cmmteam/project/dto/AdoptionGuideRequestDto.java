package cmmteam.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdoptionGuideRequestDto {

    @NotBlank(message = "指南标题不能为空。")
    @Size(max = 255, message = "指南标题长度不能超过255个字符。")
    private String title;

    @NotBlank(message = "指南内容不能为空。")
    private String content;
}