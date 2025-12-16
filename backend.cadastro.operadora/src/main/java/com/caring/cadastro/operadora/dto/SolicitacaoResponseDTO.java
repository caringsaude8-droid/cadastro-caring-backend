package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.TipoMovimentacao;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

public class SolicitacaoResponseDTO {
    public Long id;
    public String numeroSolicitacao;
    public Long beneficiarioId;
    public String beneficiarioCpf;
    public String beneficiarioNome;
    public TipoMovimentacao tipo;
    public StatusSolicitacao status;
    public String motivoExclusao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    public Date dataSolicitacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    public Date dataAprovacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    public Date dataEfetivacao;

    public String usuarioSolicitanteNome;
    public String aprovadorNome;
    public String observacoesSolicitacao;
    public String observacoesAprovacao;
    public Long empresaId;

    // Dados do beneficiário para contexto (opcional)
    public BeneficiarioResponseDTO beneficiarioAtual;
    public String dadosJson;
    public List<BenAnexoDTO> anexos;
}
