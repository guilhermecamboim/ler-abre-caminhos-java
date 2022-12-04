package com.guilherme.lerabrecaminhos.service;

import com.guilherme.lerabrecaminhos.controller.impl.UploadFileRepository;
import com.guilherme.lerabrecaminhos.entities.UploadFile;
import com.guilherme.lerabrecaminhos.exceptions.FileStorageException;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final UploadFileRepository repository;

    private final MinioService minioService;

    private final MinioClient minioClient;

    @Value("${spring.minio.bucketName}")
    private String bucketName;

    public UploadFile storeFile(MultipartFile file) {
        var originalName = file.getOriginalFilename() == null ? "unnamed" : file.getOriginalFilename();
        var fileName = StringUtils.cleanPath(originalName);
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            UploadFile uploadFile = new UploadFile(null, UUID.randomUUID() + "-" + fileName, file.getContentType());
            Path path = Path.of(uploadFile.getFileName());
            Map<String, String> header = new HashMap<>();
            header.put("X-Incident-Id", "C918371984");
            repository.save(uploadFile);
            uploadFile.setFileUrl(getUrl(uploadFile.getId()));
            repository.save(uploadFile);

            minioService.upload(path, file.getInputStream(), file.getContentType());
            return uploadFile;
        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
        } catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }
    }

    public UploadFile updateFile(MultipartFile file, Integer id) throws MinioException, IOException, FileStorageException {
        UploadFile uploadFile = this.getFile(id);
        Path pathRemove = Path.of(uploadFile.getFileName());
        minioService.remove(pathRemove);

        var originalName = file.getOriginalFilename() == null ? "unnamed" : file.getOriginalFilename();
        var fileName = StringUtils.cleanPath(originalName);
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
        uploadFile.setFileName(UUID.randomUUID() + "-" + fileName);
        uploadFile.setFileType(file.getContentType());

        Path path = Path.of(uploadFile.getFileName());
        Map<String, String> header = new HashMap<>();
        header.put("X-Incident-Id", "C918371984");
        uploadFile.setFileUrl(getUrl(uploadFile.getId()));

        try {
            minioService.upload(path, file.getInputStream(), file.getContentType());
        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
        } catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }
        return repository.save(uploadFile);
    }

    public UploadFile getFile(Integer fileId) {
        return repository.findById(fileId)
                .orElseThrow();
    }

    public String getUrl(Integer id) throws IOException {
        Optional<UploadFile> uploadFile = repository.findById(id);
        if (uploadFile.isEmpty()) {
            return null;
        }
        String url;
        try {
            url = minioClient.getObjectUrl(bucketName, uploadFile.get().getFileName());
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException |
                 NoResponseException | XmlPullParserException | ErrorResponseException |
                 InternalException | java.security.InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public void deleteFile(Integer id) throws MinioException {
        Optional<UploadFile> uploadFile = repository.findById(id);
        if (uploadFile.isPresent()) {
            Path pathRemove = Path.of(uploadFile.get().getFileName());
            minioService.remove(pathRemove);
        }
    }

    public ResponseEntity<Resource> downloadFile(Integer fileId){
        Optional<UploadFile> uploadFile = repository.findById(fileId);
        return uploadFile.map(this::downloadFile).orElse(null);
    }

    public ResponseEntity<Resource> downloadFile(UploadFile file) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(new ByteArrayResource(minioService.get(Path.of(file.getFileName())).readAllBytes()));

        } catch (MinioException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

