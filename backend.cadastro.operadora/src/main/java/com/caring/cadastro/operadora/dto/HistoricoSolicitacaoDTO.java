package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoHistorico.TipoOperacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class HistoricoSolicitacaoDTO {
    public Long id;
    public Long solicitacaoId;
    public TipoOperacao operacao;
    public String statusAnterior;
    public String statusNovo;
    public String campoAlterado;
    public String valorAnterior;
    public String valorNovo;
    public String usuarioNome;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    public Date dataOperacao;

    public String observacoes;
    public String ipOrigem;

    // Campos calculados para o frontend
    public String descricaoOperacao;
    public String tipoOperacaoDescricao;
}
