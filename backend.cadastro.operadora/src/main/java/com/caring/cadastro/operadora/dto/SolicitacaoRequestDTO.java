package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.TipoMovimentacao;

public class SolicitacaoRequestDTO {
    public Long beneficiarioId; // null para inclusões
    public TipoMovimentacao tipo;
    public String motivoExclusao; // Apenas para exclusões
    public String observacoesSolicitacao;

    // Dados completos do beneficiário (para inclusão/alteração)
    public Object dadosPropostos; //

    // Novo campo para permitir envio de empresaId pelo frontend
    public Long empresaId;
}
