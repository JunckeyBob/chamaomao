package cmmteam.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

@Data
public class UserLoginRequestDto {

    @NotBlank(message = "手机号码不能为空。")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确。")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空。")
    @ToString.Exclude // For security
    private String password;
}