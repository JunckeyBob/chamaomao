package cmmteam.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class PasswordUpdateRequestDto {

    @NotBlank(message = "当前密码不能为空。")
    @ToString.Exclude
    private String currentPassword;

    @NotBlank(message = "新密码不能为空。")
    @Size(min = 6, max = 20, message = "新密码长度必须在6到20位之间。")
    @ToString.Exclude
    private String newPassword;

    @NotBlank(message = "新密码确认不能为空。")
    @ToString.Exclude
    private String confirmNewPassword;
}