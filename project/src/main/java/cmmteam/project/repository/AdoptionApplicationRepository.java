package cmmteam.project.repository;

import cmmteam.project.entity.AdoptionApplication;
import cmmteam.project.entity.Animal;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.AdoptionStatusApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Integer> {

    List<AdoptionApplication> findByApplicantUser(User applicantUser);

    List<AdoptionApplication> findByApplicantUser_Id(Integer applicantUserId);

    List<AdoptionApplication> findByAnimal(Animal animal);

    List<AdoptionApplication> findByAnimal_AnimalId(Integer animalId);

    List<AdoptionApplication> findByStatus(AdoptionStatusApplication status);

    Optional<AdoptionApplication> findByApplicantUserAndAnimalAndStatus(User applicantUser, Animal animal, AdoptionStatusApplication status);

    List<AdoptionApplication> findByAnimalAndStatus(Animal animal, AdoptionStatusApplication status);
}