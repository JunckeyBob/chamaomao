package cmmteam.project.repository;

import cmmteam.project.entity.Animal;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import cmmteam.project.entity.enums.AnimalGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {

    List<Animal> findByNameContainingIgnoreCase(String nameKeyword);

    List<Animal> findBySpecies(String species);

    List<Animal> findByBreed(String breed);

    List<Animal> findByGender(AnimalGender gender);

    List<Animal> findByAdoptionStatus(AdoptionStatusAnimal adoptionStatus);

    List<Animal> findByManagedByUser(User managedByUser);
}