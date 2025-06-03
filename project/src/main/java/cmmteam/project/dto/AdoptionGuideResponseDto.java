package cmmteam.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionGuideResponseDto {
    private int guideId;
    private String title;
    private String content;
    private int authorId;
    private LocalDateTime publishDate;
    private LocalDateTime lastModifiedDate;
}