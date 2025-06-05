package cmmteam.project.dto;

import cmmteam.project.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponseDto {

    @NotNull(message = "用户ID不能为空。")
    private Integer usersId;

    @NotNull(message = "手机号码不能为空。")
    private String phoneNumber;

    private String nickname;

    private String address;

    private LocalDateTime registrationDate;

    private UserRole role;

    public UserProfileResponseDto(Integer usersId, String phoneNumber, String nickname, String address, LocalDateTime registrationDate, UserRole role) {
        this.usersId = usersId;
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.address = address;
        this.registrationDate = registrationDate;
        this.role = role;
    }
}