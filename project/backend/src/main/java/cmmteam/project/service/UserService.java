package cmmteam.project.service;

import cmmteam.project.dto.UserLoginRequestDto;
import cmmteam.project.dto.UserProfileUpdateRequestDto;
import cmmteam.project.dto.UserRegistrationRequestDto;
import cmmteam.project.dto.PasswordUpdateRequestDto;

public interface UserService {

    void registerUser(UserRegistrationRequestDto registrationRequest);

    String loginUser(UserLoginRequestDto loginRequest);

    void updateUserProfile(int userId, UserProfileUpdateRequestDto profileUpdateRequest);

    void updateUserPassword(int userId, PasswordUpdateRequestDto passwordUpdateRequest);

}