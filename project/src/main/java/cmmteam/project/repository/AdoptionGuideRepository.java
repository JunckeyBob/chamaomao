package cmmteam.project.repository;

import cmmteam.project.entity.AdoptionGuide;
import cmmteam.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionGuideRepository extends JpaRepository<AdoptionGuide, Integer> {

    List<AdoptionGuide> findByAuthorUser(User authorUser);

    List<AdoptionGuide> findByTitleContainingIgnoreCase(String titleKeyword);
}