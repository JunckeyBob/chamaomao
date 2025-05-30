package cmmteam.project.entity;

import cmmteam.project.entity.enums.AccountStatus;
import cmmteam.project.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    @EqualsAndHashCode.Include
    private Integer usersId;

    @Column(name = "phone_number", length = 20, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "accout_status", length = 20, nullable = false)
    private AccountStatus accountStatus;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Lob
    @Column(name = "pet_experience", columnDefinition = "TEXT")
    private String petExperience;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    // --- Relationships ---

    // User (1) --manage-- (N) Animal
    @OneToMany(mappedBy = "managedByUser", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private List<Animal> managedAnimals = new ArrayList<>();

    // User (N) --usr_adpot (applicant_user_id)-- (N) AdoptionApplication
    @OneToMany(mappedBy = "applicantUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<AdoptionApplication> applicationsAsApplicant = new ArrayList<>();

    // User (N) --usr_adpot (reviewed_by)-- (N) AdoptionApplication
    @OneToMany(mappedBy = "reviewedByUser", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private List<AdoptionApplication> applicationsAsReviewer = new ArrayList<>();

    // User (1) --report-- (N) AnimalLocation
    @OneToMany(mappedBy = "reportedByUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<AnimalLocation> reportedLocations = new ArrayList<>();

    // User (1) --author-- (N) AdoptionGuide
    @OneToMany(mappedBy = "authorUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<AdoptionGuide> authoredGuides = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.phoneNumber; // as Spring Security's username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;    //never expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountStatus != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    //never expire
    }

    @Override
    public boolean isEnabled() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public Integer getId() {
        return this.usersId;
    }
}