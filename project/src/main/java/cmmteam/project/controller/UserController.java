package cmmteam.project.controller;

import cmmteam.project.dto.UserLoginRequestDto;
import cmmteam.project.dto.UserProfileUpdateRequestDto;
import cmmteam.project.dto.UserRegistrationRequestDto;
import cmmteam.project.dto.PasswordUpdateRequestDto;
import cmmteam.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequestDto registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("用户注册成功！");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册过程中发生错误。");
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginRequestDto loginRequest) {
        try {
            String loginResult = userService.loginUser(loginRequest);
            return ResponseEntity.ok(loginResult);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录过程中发生错误。");
        }
    }

    // Update Profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequestDto profileUpdateRequest) {
        // Just for test now!
        int currentUserId = 1;

        try {
            userService.updateUserProfile(currentUserId, profileUpdateRequest);
            return ResponseEntity.ok("用户信息更新成功！");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新用户信息时发生错误。");
        }
    }

    // Update Password
    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(@Valid @RequestBody PasswordUpdateRequestDto passwordUpdateRequest){
        // Just for test now!
        int currentUserId = 1;
        try {
            userService.updateUserPassword(currentUserId, passwordUpdateRequest);
            return ResponseEntity.ok("密码更新成功！");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新密码时发生错误。");
        }
    }

}