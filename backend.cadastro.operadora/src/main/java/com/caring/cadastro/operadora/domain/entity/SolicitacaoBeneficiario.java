package com.caring.cadastro.operadora.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SGI_SOLICITACOES_BENEFICIARIO")
public class SolicitacaoBeneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGI_SOLICITACOES_BENEFICIARIO_ID_SEQ")
    @SequenceGenerator(name = "SGI_SOLICITACOES_BENEFICIARIO_ID_SEQ", sequenceName = "SGI_SOLICITACOES_BENEFICIARIO_ID_SEQ", allocationSize = 1)
    @Column(name = "SOL_ID")
    private Long id;

    @Column(name = "SOL_NUMERO", unique = true)
    private String numeroSolicitacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOL_BEN_ID")
    private Beneficiario beneficiario;

    @Column(name = "SOL_BEN_CPF")
    private String beneficiarioCpf;

    @Column(name = "SOL_BEN_NOME")
    private String beneficiarioNome;

    @Enumerated(EnumType.STRING)
    @Column(name = "SOL_TIPO")
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "SOL_STATUS")
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @Column(name = "SOL_MOTIVO_EXCLUSAO")
    private String motivoExclusao;

    @Column(name = "SOL_OBSERVACOES")
    private String observacoes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SOL_DATA_SOLICITACAO")
    private Date dataSolicitacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "SOL_DATA_APROVACAO")
    private Date dataAprovacao;

    @Temporal(TemporalType.DATE)
    @Column(name = "SOL_DATA_EFETIVACAO")
    private Date dataEfetivacao;

    @Column(name = "SOL_USUARIO_SOLICITANTE_ID")
    private Long usuarioSolicitanteId;

    @Column(name = "SOL_USUARIO_SOLICITANTE_NOME")
    private String usuarioSolicitanteNome;

    @Column(name = "SOL_APROVADOR_ID")
    private Long aprovadorId;

    @Column(name = "SOL_APROVADOR_NOME")
    private String aprovadorNome;

    @Column(name = "SOL_OBSERVACOES_SOLICITACAO")
    private String observacoesSolicitacao;

    @Column(name = "SOL_OBSERVACOES_APROVACAO")
    private String observacoesAprovacao;

    @Lob
    @Column(name = "SOL_DADOS_JSON")
    private String dadosJson;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SOL_CREATED_AT")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SOL_UPDATED_AT")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOL_EMP_ID")
    private Empresa empresa;

    public enum TipoMovimentacao {
        INCLUSAO, ALTERACAO, EXCLUSAO
    }

    public enum StatusSolicitacao {
        PENDENTE, APROVADA, REJEITADA, CANCELADA
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        if (dataSolicitacao == null) {
            dataSolicitacao = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @Override
    public String toString() {
        return "SolicitacaoBeneficiario{" +
                "id=" + id +
                ", numeroSolicitacao='" + numeroSolicitacao + '\'' +
                ", beneficiarioId=" + (beneficiario != null ? beneficiario.getId() : null) +
                ", beneficiarioCpf='" + beneficiarioCpf + '\'' +
                ", beneficiarioNome='" + beneficiarioNome + '\'' +
                ", tipo=" + tipo +
                ", status=" + status +
                ", motivoExclusao='" + motivoExclusao + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", dataSolicitacao=" + dataSolicitacao +
                ", dataAprovacao=" + dataAprovacao +
                ", dataEfetivacao=" + dataEfetivacao +
                ", usuarioSolicitanteId=" + usuarioSolicitanteId +
                ", usuarioSolicitanteNome='" + usuarioSolicitanteNome + '\'' +
                ", aprovadorId=" + aprovadorId +
                ", aprovadorNome='" + aprovadorNome + '\'' +
                ", observacoesSolicitacao='" + observacoesSolicitacao + '\'' +
                ", observacoesAprovacao='" + observacoesAprovacao + '\'' +
                ", dadosJson='" + dadosJson + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", empresaId=" + (empresa != null ? empresa.getId() : null) +
                '}';
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroSolicitacao() { return numeroSolicitacao; }
    public void setNumeroSolicitacao(String numeroSolicitacao) { this.numeroSolicitacao = numeroSolicitacao; }

    public Beneficiario getBeneficiario() { return beneficiario; }
    public void setBeneficiario(Beneficiario beneficiario) { this.beneficiario = beneficiario; }

    public String getBeneficiarioCpf() { return beneficiarioCpf; }
    public void setBeneficiarioCpf(String beneficiarioCpf) { this.beneficiarioCpf = beneficiarioCpf; }

    public String getBeneficiarioNome() { return beneficiarioNome; }
    public void setBeneficiarioNome(String beneficiarioNome) { this.beneficiarioNome = beneficiarioNome; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }

    public String getMotivoExclusao() { return motivoExclusao; }
    public void setMotivoExclusao(String motivoExclusao) { this.motivoExclusao = motivoExclusao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Date getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(Date dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public Date getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(Date dataAprovacao) { this.dataAprovacao = dataAprovacao; }

    public Date getDataEfetivacao() { return dataEfetivacao; }
    public void setDataEfetivacao(Date dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }

    public Long getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public void setUsuarioSolicitanteId(Long usuarioSolicitanteId) { this.usuarioSolicitanteId = usuarioSolicitanteId; }

    public String getUsuarioSolicitanteNome() { return usuarioSolicitanteNome; }
    public void setUsuarioSolicitanteNome(String usuarioSolicitanteNome) { this.usuarioSolicitanteNome = usuarioSolicitanteNome; }

    public Long getAprovadorId() { return aprovadorId; }
    public void setAprovadorId(Long aprovadorId) { this.aprovadorId = aprovadorId; }

    public String getAprovadorNome() { return aprovadorNome; }
    public void setAprovadorNome(String aprovadorNome) { this.aprovadorNome = aprovadorNome; }

    public String getObservacoesSolicitacao() { return observacoesSolicitacao; }
    public void setObservacoesSolicitacao(String observacoesSolicitacao) { this.observacoesSolicitacao = observacoesSolicitacao; }

    public String getObservacoesAprovacao() { return observacoesAprovacao; }
    public void setObservacoesAprovacao(String observacoesAprovacao) { this.observacoesAprovacao = observacoesAprovacao; }

    public String getDadosJson() { return dadosJson; }
    public void setDadosJson(String dadosJson) { this.dadosJson = dadosJson; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
