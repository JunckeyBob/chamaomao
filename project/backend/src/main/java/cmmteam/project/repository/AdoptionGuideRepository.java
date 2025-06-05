package cmmteam.project.repository;

import cmmteam.project.entity.AdoptionGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionGuideRepository extends JpaRepository<AdoptionGuide, Integer> {

    List<AdoptionGuide> findByTitleContainingIgnoreCase(String titleKeyword);

    List<AdoptionGuide> findAllByOrderByPublishDateDesc();
}