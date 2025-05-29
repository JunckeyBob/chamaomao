package cmmteam.project.dto;

import cmmteam.project.entity.enums.AnimalGender;
import cmmteam.project.entity.enums.AdoptionStatusAnimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class AnimalRequestDto {

    @NotBlank(message = "动物名称不能为空。")
    @Size(max = 100, message = "动物名称长度不能超过100个字符。")
    private String name;

    @NotBlank(message = "动物种类不能为空。")
    @Size(max = 50, message = "动物种类长度不能超过50个字符。")
    private String species;

    @Size(max = 50, message = "动物品种长度不能超过50个字符。")
    private String breed;

    @NotBlank(message = "动物年龄描述不能为空。")
    @Size(max = 50, message = "动物年龄描述长度不能超过50个字符。")
    private String age;

    @NotNull(message = "动物性别不能为空。")
    private AnimalGender gender;

    @NotBlank(message = "动物健康状况描述不能为空。")
    private String healthStatus;

    private AdoptionStatusAnimal adoptionStatus;

    @Size(max = 500, message = "性格特点描述长度不能超过500个字符。")
    private String characteristics;

    private List<String> photoUrls;

    private AnimalLocationRequestDto initialLocation;
}