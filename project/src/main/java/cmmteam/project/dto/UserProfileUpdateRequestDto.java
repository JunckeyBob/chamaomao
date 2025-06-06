package cmmteam.project.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequestDto {

    @Size(min = 1, max = 50, message = "昵称长度必须在1到50个字符之间。")
    private String nickname; // Optional

    @Size(max = 255, message = "地址长度最多为255个字符。")
    private String address; // Optional

    private String petExperience; // Optional


}