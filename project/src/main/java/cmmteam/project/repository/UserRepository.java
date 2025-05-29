package cmmteam.project.repository;

import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.entity.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole(UserRole role);

    List<User> findByAccountStatus(AccountStatus accountStatus);

    Optional<User> findByNickname(String nickname);

}