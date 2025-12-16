package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.TipoMovimentacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitacaoRequestDTO {
    public Long beneficiarioId; // null para inclusões
    public TipoMovimentacao tipo;
    public String motivoExclusao; // Apenas para exclusões
    public String observacoesSolicitacao;
    public Object dadosPropostos; //

    // Novo campo para permitir envio de empresaId pelo frontend
    public Long empresaId;

    // Campos adicionais para receber dados do front
    public String beneficiarioNome;
    public String beneficiarioCpf;
    public String observacoes;
    public String observacoesAprovacao;

    // Campo opcional para anexos
    public List<BenAnexoDTO> anexos;

    public SolicitacaoRequestDTO() {}

    @Override
    public String toString() {
        return "SolicitacaoRequestDTO{" +
                "beneficiarioId=" + beneficiarioId +
                ", tipo=" + tipo +
                ", motivoExclusao='" + motivoExclusao + '\'' +
                ", observacoesSolicitacao='" + observacoesSolicitacao + '\'' +
                ", beneficiarioNome='" + beneficiarioNome + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", observacoesAprovacao='" + observacoesAprovacao + '\'' +
                ", beneficiarioCpf='" + beneficiarioCpf + '\'' +
                ", dadosPropostos=" + dadosPropostos +
                ", empresaId=" + empresaId +
                ", anexos=" + anexos +
                '}';
    }
}
