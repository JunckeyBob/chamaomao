package cmmteam.project.config;

import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.AccountStatus;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN, for test
    private static final String ADMIN_PHONE_NUMBER = "18888888888";
    private static final String ADMIN_PASSWORD = "111111";
    private static final String ADMIN_NICKNAME = "ADMIN";

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findByPhoneNumber(ADMIN_PHONE_NUMBER).isPresent()) {
            User adminUser = new User();
            adminUser.setPhoneNumber(ADMIN_PHONE_NUMBER);
            adminUser.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            adminUser.setNickname(ADMIN_NICKNAME);
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setAccountStatus(AccountStatus.ACTIVE);
            adminUser.setRegistrationDate(LocalDateTime.now());
            adminUser.setAddress("Admin Address");
            adminUser.setPetExperience("N/A");

            userRepository.save(adminUser);
            System.out.println(">>> Created default admin user with phone number: " + ADMIN_PHONE_NUMBER);
        } else {
            System.out.println(">>> Admin user detected. Phone number: " + ADMIN_PHONE_NUMBER);
        }

    }
}