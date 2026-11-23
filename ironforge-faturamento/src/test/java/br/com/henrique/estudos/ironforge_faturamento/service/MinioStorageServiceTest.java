package br.com.henrique.estudos.ironforge_faturamento.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioStorageService minioStorageService;

    private final String BUCKET = "ironforge-faturas-test";
    private final String PUBLIC_URL = "http://localhost:9000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioStorageService, "bucketName", BUCKET);
        ReflectionTestUtils.setField(minioStorageService, "minioPublicUrl", PUBLIC_URL);
    }

    @Test
    @DisplayName("Deve fazer upload do PDF no storage com sucesso quando o bucket já existir")
    void deveFazerUploadDoPdfComSucessoQuandoBucketJaExistir() throws Exception {
        byte[] pdfBytes = "PDF TEST CONTENT".getBytes();
        String nomeArquivo = "notafiscal_pedido_123.pdf";

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        String url = minioStorageService.uploadPdf(nomeArquivo, pdfBytes);

        assertThat(url).isEqualTo(PUBLIC_URL + "/" + BUCKET + "/" + nomeArquivo);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient, never()).setBucketPolicy(any(SetBucketPolicyArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Deve criar o bucket, aplicar política pública e fazer upload do PDF quando o bucket não existir")
    void deveCriarBucketEFazerUploadQuandoBucketNaoExistir() throws Exception {
        byte[] pdfBytes = "PDF TEST CONTENT".getBytes();
        String nomeArquivo = "notafiscal_pedido_123.pdf";

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        String url = minioStorageService.uploadPdf(nomeArquivo, pdfBytes);

        assertThat(url).isEqualTo(PUBLIC_URL + "/" + BUCKET + "/" + nomeArquivo);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).setBucketPolicy(any(SetBucketPolicyArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer falha ao interagir com o storage")
    void deveLancarExcecaoQuandoMinioFalhar() throws Exception {
        byte[] pdfBytes = "PDF TEST CONTENT".getBytes();
        String nomeArquivo = "notafiscal_pedido_123.pdf";

        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("MinIO offline"));

        assertThatThrownBy(() -> minioStorageService.uploadPdf(nomeArquivo, pdfBytes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Falha no envio do PDF para o storage");
    }
}