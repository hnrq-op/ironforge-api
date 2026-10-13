package br.com.henrique.estudos.ironforge_faturamento.service.dto;

import java.math.BigDecimal;

public record ItemNotaFiscalReportDTO(
        String sku,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {

    public String getSku() {
        return sku;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}