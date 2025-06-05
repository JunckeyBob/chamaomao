package cmmteam.project.controller;

import cmmteam.project.dto.UserLoginRequestDto;
import cmmteam.project.dto.UserProfileUpdateRequestDto;
import cmmteam.project.dto.UserRegistrationRequestDto;
import cmmteam.project.dto.PasswordUpdateRequestDto;
import cmmteam.project.entity.User;
import cmmteam.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
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
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginRequestDto loginRequest,
            HttpServletRequest httpRequest) {
        try {
            String loginResultMessage = userService.loginUser(loginRequest);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {

                // get Session
                HttpSession session = httpRequest.getSession(true); // create if not exists

                // put SecurityContext (has Authentication in it) into Session
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext());

                System.out.println("UserController: Login successful. Session created/retrieved: " + session.getId());
                System.out.println("UserController: SecurityContext explicitly set in HttpSession.");
                System.out.println("UserController: Authentication principal in session: " +
                        ((org.springframework.security.core.context.SecurityContext) session
                                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
                                .getAuthentication().getPrincipal());

            } else {
                System.err.println(
                        "UserController: Authentication not found or not truly authenticated in SecurityContextHolder after loginUser call. This is unexpected.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录后未能正确建立安全上下文。");
            }

            return ResponseEntity.ok(loginResultMessage);

        } catch (RuntimeException e) {
            System.err.println("UserController: Login failed - " + e.getMessage());
            if (e.getCause() instanceof org.springframework.security.core.AuthenticationException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败：" + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录过程中发生内部错误。");
        }
    }

    private User getCurrentUserFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    // Update Profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequestDto profileUpdateRequest,
            Authentication authentication) {
        User currentUser = getCurrentUserFromAuth(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证。");
        }
        Integer currentUserId = currentUser.getId();

        try {
            userService.updateUserProfile(currentUserId, profileUpdateRequest);
            return ResponseEntity.ok("用户信息更新成功！");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update Password
    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(@Valid @RequestBody PasswordUpdateRequestDto passwordUpdateRequest,
            Authentication authentication) {
        User currentUser = getCurrentUserFromAuth(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证。");
        }
        Integer currentUserId = currentUser.getId();

        try {
            userService.updateUserPassword(currentUserId, passwordUpdateRequest);
            return ResponseEntity.ok("密码更新成功！");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}