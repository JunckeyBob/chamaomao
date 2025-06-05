package cmmteam.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "adoption_guid")
public class AdoptionGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    @EqualsAndHashCode.Include
    private Integer guideId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    // AdoptionGuid (N) --author-- (1) User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", referencedColumnName = "users_id", nullable = false)
    @ToString.Exclude
    private User authorUser;

    @CreationTimestamp
    @Column(name = "publish_date", nullable = false, updatable = false)
    private LocalDateTime publishDate;
}