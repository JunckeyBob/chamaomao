package cmmteam.project.entity;

import cmmteam.project.entity.enums.AdoptionStatusApplication;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "adoption_application")
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adoption_id")
    @EqualsAndHashCode.Include
    private Integer adoptionId;

    // AdoptionApplication (N) --usr_adpot (applicant_user_id)-- (N) User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_user_id", referencedColumnName = "users_id", nullable = false)
    @ToString.Exclude
    private User applicantUser;

    // AdoptionApplication (N) --adopt_animal-- (1) Animal
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", referencedColumnName = "animal_id", nullable = false)
    @ToString.Exclude
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AdoptionStatusApplication status;

    // AdoptionApplication (N) --usr_adpot (reviewed_by)-- (N) User
    @ManyToOne(fetch = FetchType.LAZY) // Can be null if not yet reviewed
    @JoinColumn(name = "reviewed_by", referencedColumnName = "users_id")
    @ToString.Exclude
    private User reviewedByUser;

    @Lob
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Lob
    @Column(name = "review_details", columnDefinition = "TEXT") // By ADMIN
    private String reviewDetails;

    @CreationTimestamp
    @Column(name = "application_date", nullable = false, updatable = false) // Added for audit
    private LocalDateTime applicationDate;

    @UpdateTimestamp
    @Column(name = "review_date")
    private LocalDateTime reviewDate;
}