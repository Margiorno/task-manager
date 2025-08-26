package com.pm.todoapp.files.adapter;


import com.pm.todoapp.domain.file.dto.FileType;
import com.pm.todoapp.domain.file.port.FileStoragePort;
import com.pm.todoapp.files.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class FileStorageAdapter implements FileStoragePort {
    private final FileService fileService;

    @Override
    public String saveFile(MultipartFile file, FileType fileType) {
        return fileService.saveFile(file, fileType);
    }

    @Override
    public void deleteFile(String filePath, FileType fileType) {
        fileService.deleteFile(filePath, fileType);
    }
}
