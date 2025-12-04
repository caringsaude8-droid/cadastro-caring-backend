package com.caring.cadastro.operadora.dto;

public class ProcessarSolicitacaoDTO {
    public String acao; // "APROVAR" ou "REJEITAR"
    public String observacoesAprovacao;
    public Object dadosAprovacao; // Novo campo para dados extras na aprovação
}
