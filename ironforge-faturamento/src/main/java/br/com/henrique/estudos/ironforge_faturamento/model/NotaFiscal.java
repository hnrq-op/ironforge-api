package br.com.henrique.estudos.ironforge_faturamento.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_nota_fiscal")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private UUID pedidoId;

    @Column(name = "numero_nota", length = 100, nullable = false, unique = true)
    private String numeroNota;

    @Column(name = "chave_acesso", length = 44, nullable = false, unique = true)
    private String chaveAcesso;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "data_emissao", nullable = false)
    private OffsetDateTime dataEmissao;

    @PrePersist
    public void prePersist() {
        if (dataEmissao == null) {
            dataEmissao = OffsetDateTime.now();
        }
    }
}