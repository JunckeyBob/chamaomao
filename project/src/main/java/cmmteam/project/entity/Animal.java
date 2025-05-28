package cmmteam.project.entity;

import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import cmmteam.project.entity.enums.AnimalGender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    @EqualsAndHashCode.Include
    private Integer animalId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "specise", length = 50, nullable = false)
    private String specise;

    @Column(name = "breed", length = 50)
    private String breed;

    @Column(name = "age", length = 50)
    private String age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    private AnimalGender gender;

    @Lob
    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "adoption_status", length = 20, nullable = false)
    private AdoptionStatusAnimal adoptionStatus;

    // --- Relationships ---

    // Animal (N) --manage-- (1) User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_by", referencedColumnName = "users_id") // FK in Animal table
    @ToString.Exclude
    private User managedByUser;

    // Animal (1) --adopt_animal-- (N) AdoptionApplication
    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<AdoptionApplication> adoptionApplications = new ArrayList<>();

    // Animal (1) --record-- (N) AnimalLocation
    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AnimalLocation> locations = new ArrayList<>();

    // Animal (1) --has_photo-- (N) AnimalPhoto
    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AnimalPhoto> photos = new ArrayList<>();
}