package com.guilherme.lerabrecaminhos.service;

import com.guilherme.lerabrecaminhos.controller.impl.UploadFileRepository;
import com.guilherme.lerabrecaminhos.entities.UploadFile;
import com.guilherme.lerabrecaminhos.exceptions.FileStorageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final UploadFileRepository repository;

    private final MinioClient minioClient;

    @Value("${spring.minio.bucketName}")
    private String bucketName;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Value("${spring.minio.url}")
    private String minioUrl;

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

            upload(path, file.getInputStream(), file.getContentType());
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
        remove(pathRemove);

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
            upload(path, file.getInputStream(), file.getContentType());
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
        // minio 8+ não tem mais getObjectUrl: monta a mesma URL do minio 6 (endpoint/bucket/objeto codificado)
        String url = minioUrl.replaceAll("/+$", "") + "/" + bucketName + "/" + encodeObjectName(uploadFile.get().getFileName());
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public void deleteFile(Integer id) throws MinioException {
        Optional<UploadFile> uploadFile = repository.findById(id);
        if (uploadFile.isPresent()) {
            Path pathRemove = Path.of(uploadFile.get().getFileName());
            remove(pathRemove);
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
                    .body(new ByteArrayResource(get(Path.of(file.getFileName()))));

        } catch (MinioException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Operações que antes vinham do MinioService do spring-boot-starter-minio, agora com o cliente oficial (minio 9).
    private void upload(Path path, InputStream inputStream, String contentType) throws MinioException {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(path.toString())
                .stream(inputStream, null, 10L * 1024 * 1024)
                .contentType(contentType)
                .build());
    }

    private void remove(Path path) throws MinioException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path.toString()).build());
    }

    private byte[] get(Path path) throws MinioException, IOException {
        // fecha o stream para liberar a conexão HTTP do cliente
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(path.toString()).build())) {
            return inputStream.readAllBytes();
        }
    }

    private static String encodeObjectName(String objectName) {
        return URLEncoder.encode(objectName, StandardCharsets.UTF_8).replace("+", "%20").replace("%2F", "/");
    }
}
