package cmmteam.project.dto;
import lombok.Data;
import java.util.List;

@Data
public class AnimalImageRecognitionResponseDto {
    private String identifiedSpecies;
    private String identifiedBreed;
    private Float confidenceScore;
    private List<Long> matchingAnimalIdsInDb;
    private String errorMessage;
}