package com.caring.cadastro.operadora.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SGI_SOLICITACOES_HISTORICO")
public class SolicitacaoHistorico {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGI_SOLICITACOES_HISTORICO_ID_SEQ")
    @SequenceGenerator(name = "SGI_SOLICITACOES_HISTORICO_ID_SEQ", sequenceName = "SGI_SOLICITACOES_HISTORICO_ID_SEQ", allocationSize = 1)
    @Column(name = "HIS_ID")
    private Long id;

    @Column(name = "HIS_SOL_ID")
    private Long solicitacaoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "HIS_OPERACAO")
    private TipoOperacao operacao;

    @Column(name = "HIS_STATUS_ANTERIOR")
    private String statusAnterior;

    @Column(name = "HIS_STATUS_NOVO")
    private String statusNovo;

    @Column(name = "HIS_CAMPO_ALTERADO")
    private String campoAlterado;

    @Lob
    @Column(name = "HIS_VALOR_ANTERIOR")
    private String valorAnterior;

    @Lob
    @Column(name = "HIS_VALOR_NOVO")
    private String valorNovo;

    @Column(name = "HIS_USUARIO_ID")
    private Long usuarioId;

    @Column(name = "HIS_USUARIO_NOME")
    private String usuarioNome;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "HIS_DATA_OPERACAO")
    private Date dataOperacao;

    @Column(name = "HIS_IP_ORIGEM")
    private String ipOrigem;

    @Lob
    @Column(name = "HIS_OBSERVACOES")
    private String observacoes;

    @Lob
    @Column(name = "HIS_SNAPSHOT_COMPLETO")
    private String snapshotCompleto;

    public enum TipoOperacao {
        INSERT, UPDATE, DELETE
    }

    @PrePersist
    protected void onCreate() {
        if (dataOperacao == null) {
            dataOperacao = new Date();
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSolicitacaoId() { return solicitacaoId; }
    public void setSolicitacaoId(Long solicitacaoId) { this.solicitacaoId = solicitacaoId; }

    public TipoOperacao getOperacao() { return operacao; }
    public void setOperacao(TipoOperacao operacao) { this.operacao = operacao; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }

    public String getCampoAlterado() { return campoAlterado; }
    public void setCampoAlterado(String campoAlterado) { this.campoAlterado = campoAlterado; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNovo() { return valorNovo; }
    public void setValorNovo(String valorNovo) { this.valorNovo = valorNovo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }

    public Date getDataOperacao() { return dataOperacao; }
    public void setDataOperacao(Date dataOperacao) { this.dataOperacao = dataOperacao; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getSnapshotCompleto() { return snapshotCompleto; }
    public void setSnapshotCompleto(String snapshotCompleto) { this.snapshotCompleto = snapshotCompleto; }
}
