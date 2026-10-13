package br.com.henrique.estudos.ironforge_faturamento.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:ironforge-faturas}")
    private String bucketName;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    public String uploadPdf(String nomeArquivo, byte[] pdfBytes) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                log.info("Bucket '{}' não encontrado no MinIO. Criando bucket...", bucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }

            try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(nomeArquivo)
                                .stream(inputStream, pdfBytes.length, -1)
                                .contentType("application/pdf")
                                .build()
                );
            }

            String fileUrl = String.format("%s/%s/%s", minioEndpoint, bucketName, nomeArquivo);
            log.info("PDF salvo no MinIO com sucesso: {}", fileUrl);

            return fileUrl;

        } catch (Exception e) {
            log.error("Erro ao realizar upload do arquivo {} para o MinIO: {}", nomeArquivo, e.getMessage(), e);
            throw new RuntimeException("Falha no envio do PDF para o MinIO", e);
        }
    }
}