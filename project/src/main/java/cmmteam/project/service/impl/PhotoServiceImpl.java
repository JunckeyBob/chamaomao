package cmmteam.project.service.impl;

import cmmteam.project.entity.Animal;
import cmmteam.project.entity.AnimalPhoto;
import cmmteam.project.entity.User;
import cmmteam.project.entity.enums.UserRole;
import cmmteam.project.repository.AnimalPhotoRepository;
import cmmteam.project.repository.AnimalRepository;
import cmmteam.project.service.PhotoService;
import cmmteam.project.dto.AnimalPhotoDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final AnimalPhotoRepository animalPhotoRepository;
    private final AnimalRepository animalRepository;
    
    @Value("${file.upload-dir}") // 从配置文件中读取文件存储路径
    private String uploadDir;

    @Autowired
    public PhotoServiceImpl(AnimalPhotoRepository animalPhotoRepository,
                           AnimalRepository animalRepository) {
        this.animalPhotoRepository = animalPhotoRepository;
        this.animalRepository = animalRepository;
    }

    private AnimalPhotoDto convertToDto(AnimalPhoto photo) {
        if (photo == null) return null;
        Animal animal = photo.getAnimal();
        AnimalPhotoDto dto = new AnimalPhotoDto();
        if (animal != null) {
            dto.setAnimalId(animal.getAnimalId());
        }
        dto.setPhotoId(photo.getPhotoId());
        dto.setPhotoUrl(photo.getPhotoUrl());
        return dto;
    }

    @Override
    @Transactional
    public AnimalPhotoDto uploadPhoto(Integer animalId, MultipartFile file, Authentication authentication) {
        // 1. 验证动物是否存在
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + animalId + " 的动物"));
        
        // 2. 验证用户权限 (示例: 只有管理员或动物创建者可以上传)
        User currentUser = (User) authentication.getPrincipal();
        if (!(currentUser.getRole() == UserRole.ADMIN) && !animal.getManagedByUser().getUsername().equals(currentUser.getUsername())) {
            throw new RuntimeException("您没有权限为此动物上传照片");
        }

        // 3. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // 4. 创建存储目录(如果不存在)
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 5. 存储文件
            Path filePath = uploadPath.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());
            
            // 6. 创建并保存照片记录
            AnimalPhoto photo = new AnimalPhoto();
            photo.setAnimal(animal);
            photo.setPhotoUrl("/uploads/" + uniqueFileName); // 访问URL
            
            return convertToDto(animalPhotoRepository.save(photo));
            
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AnimalPhotoDto> getPhotosByAnimalId(Integer animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new RuntimeException("未找到ID为 " + animalId + " 的动物");
        }
        return animalPhotoRepository.findByAnimalAnimalId(animalId).stream()
                    .map(this::convertToDto) // 使用转换函数
                    .collect(Collectors.toList()); // 收集结果到 List<B>
    }

    @Override
    @Transactional
    public void deletePhoto(Integer animalId, Integer photoId, Authentication authentication) {
        // 1. 验证照片是否存在且属于指定动物
        AnimalPhoto photo = animalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + photoId + " 的照片"));
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + animalId + " 的动物"));
        
        if (!photo.getAnimal().getAnimalId().equals(animalId)) {
            throw new IllegalArgumentException("照片不属于指定的动物");
        }
        
        // 2. 验证用户权限 (只有管理员或照片上传者可以删除)

        User currentUser = (User) authentication.getPrincipal();
        if (!(currentUser.getRole() == UserRole.ADMIN) && !animal.getManagedByUser().getUsername().equals(currentUser.getUsername())) {
            throw new RuntimeException("您没有权限删除此照片");
        }
        
        try {
            // 3. 删除物理文件
            Path filePath = Paths.get(uploadDir, photo.getPhotoUrl().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
            
            // 4. 删除数据库记录
            animalPhotoRepository.delete(photo);
        } catch (IOException e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
}