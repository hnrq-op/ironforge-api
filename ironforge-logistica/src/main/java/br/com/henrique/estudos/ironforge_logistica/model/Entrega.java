package br.com.henrique.estudos.ironforge_logistica.model;

import br.com.henrique.estudos.ironforge_logistica.model.enums.StatusEntrega;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_entrega")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private UUID pedidoId;

    @Column(name = "codigo_rastreio", length = 100, unique = true)
    private String codigoRastreio;

    @Column(name = "chave_logistica")
    private String chaveLogistica;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private StatusEntrega status;

    @Column(length = 100)
    private String transportadora;

    @Column(name = "data_envio")
    private OffsetDateTime dataEnvio;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;

    @Column(name = "data_atualizacao")
    private OffsetDateTime dataAtualizacao;

    @PrePersist
    @PreUpdate
    public void atualizarTimestamp(){
        this.dataAtualizacao = OffsetDateTime.now();
    }
}
