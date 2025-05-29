package cmmteam.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdoptionGuideResponseDto {
    private Long guideId;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime publishDate;
    private LocalDateTime lastModifiedDate;
}