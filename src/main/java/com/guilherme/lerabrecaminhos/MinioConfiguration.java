package com.guilherme.lerabrecaminhos;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Substitui a autoconfiguração do spring-boot-starter-minio 1.1 (Spring Boot 2, minio 6), que não funciona no Spring Boot 3+.
// Usa as mesmas propriedades spring.minio.* e, como o starter, verifica o bucket na inicialização.
@Configuration
public class MinioConfiguration {

    @Bean
    public MinioClient minioClient(@Value("${spring.minio.url}") String url,
                                   @Value("${spring.minio.access-key}") String accessKey,
                                   @Value("${spring.minio.secret-key}") String secretKey,
                                   @Value("${spring.minio.bucket}") String bucket) throws MinioException {
        MinioClient client = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            throw new IllegalStateException("Bucket does not exists: " + bucket);
        }
        return client;
    }
}
