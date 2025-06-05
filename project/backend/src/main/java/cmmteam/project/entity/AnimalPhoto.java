package cmmteam.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "animal_photo")
public class AnimalPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    @EqualsAndHashCode.Include
    private Integer photoId;

    // AnimalPhoto (N) --has_photo-- (1) Animal
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", referencedColumnName = "animal_id", nullable = false)
    @ToString.Exclude
    private Animal animal;

    @Column(name = "photo_url", length = 255, nullable = false)
    private String photoUrl;
}