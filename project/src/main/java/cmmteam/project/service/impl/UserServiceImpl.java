package cmmteam.project.service.impl;

import cmmteam.project.dto.UserLoginRequestDto;
import cmmteam.project.dto.UserProfileUpdateRequestDto;
import cmmteam.project.dto.UserRegistrationRequestDto;
import cmmteam.project.dto.PasswordUpdateRequestDto;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.AccountStatus;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.UserRepository;
import cmmteam.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.*;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public void registerUser(UserRegistrationRequestDto registrationRequest) {
        if (userRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("错误：该手机号已被注册！");
        }

        // create user entity
        User user = new User();
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setNickname(registrationRequest.getNickname());
        user.setAddress(registrationRequest.getAddress());
        user.setPetExperience(registrationRequest.getPetExperience());

        user.setRole(UserRole.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setRegistrationDate(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public String loginUser(UserLoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getPhoneNumber(),
                            loginRequest.getPassword()
                    )
            );
            // login success
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "登录成功！"; // Session set up

        } catch (AuthenticationException e) {
            throw new RuntimeException("登录失败：手机号或密码错误，或账户状态异常。", e);
        }
    }

    @Override
    @Transactional
    public void updateUserProfile(int userId, UserProfileUpdateRequestDto profileUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到，ID: " + userId));

        if (profileUpdateRequest.getNickname() != null && !profileUpdateRequest.getNickname().isEmpty()) {
            user.setNickname(profileUpdateRequest.getNickname());
        }
        if (profileUpdateRequest.getAddress() != null) {
            user.setAddress(profileUpdateRequest.getAddress());
        }
        if (profileUpdateRequest.getPetExperience() != null) {
            user.setPetExperience(profileUpdateRequest.getPetExperience());
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserPassword(int userId, PasswordUpdateRequestDto passwordUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到，ID: " + userId));

        if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("当前密码不正确！");
        }

        if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException("新密码和确认新密码不一致！");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

}