package cmmteam.project.dto;

import lombok.Data;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserRegistrationRequestDto {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20位之间")
    @ToString.Exclude
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 50, message = "昵称长度必须在1到50位之间")
    private String nickname;

    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address; // optional

    private String petExperience; // optional
}