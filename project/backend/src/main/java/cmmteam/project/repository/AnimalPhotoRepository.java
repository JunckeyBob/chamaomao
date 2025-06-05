package cmmteam.project.repository;

import cmmteam.project.entity.Animal;
import cmmteam.project.entity.AnimalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Integer> {

    List<AnimalPhoto> findByAnimal(Animal animal);

    List<AnimalPhoto> findByAnimalAnimalId(Integer animalId);
}