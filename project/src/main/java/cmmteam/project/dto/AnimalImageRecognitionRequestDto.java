package cmmteam.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnimalImageRecognitionRequestDto {

    @NotBlank(message = "图片数据不能为空。")
    private String imageBase64;
}