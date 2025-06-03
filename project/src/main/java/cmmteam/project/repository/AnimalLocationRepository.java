package cmmteam.project.repository;

import cmmteam.project.entity.Animal;
import cmmteam.project.entity.AnimalLocation;
import cmmteam.project.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalLocationRepository extends JpaRepository<AnimalLocation, Integer> {

    List<AnimalLocation> findByAnimalOrderByTimestampDesc(Animal animal);

    List<AnimalLocation> findByReportedByUser(User reportedByUser);

    List<AnimalLocation> findByAnimalAndTimestampBetween(Animal animal, LocalDateTime startTime, LocalDateTime endTime);

    Optional<AnimalLocation> findFirstByAnimalOrderByTimestampDesc(Animal animal);

    List<AnimalLocation> findByAnimal_AnimalIdOrderByTimestampDesc(Integer animalId);

    List<AnimalLocation> findAllByOrderByTimestampDesc(Pageable pageable);
}