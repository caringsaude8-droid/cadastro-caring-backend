package com.caring.cadastro.operadora.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SGI_BEN_ANEXOS")
public class BenAnexo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BEN_ANEXO_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOL_ID", nullable = false)
    private SolicitacaoBeneficiario solicitacao;

    @Column(name = "BEN_ANEXO_NOME_ORIGINAL", nullable = false)
    private String nomeOriginal;

    @Column(name = "BEN_ANEXO_NOME_SALVO", nullable = false)
    private String nomeSalvo;

    @Column(name = "BEN_ANEXO_CAMINHO", nullable = false)
    private String caminho;

    @Column(name = "BEN_ANEXO_TIPO_MIME")
    private String tipoMime;

    @Column(name = "BEN_ANEXO_TAMANHO")
    private Long tamanho;

    @Column(name = "BEN_ANEXO_DATA_UPLOAD")
    private LocalDateTime dataUpload;

    @Column(name = "BEN_ANEXO_USUARIO_UPLOAD")
    private String usuarioUpload;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SolicitacaoBeneficiario getSolicitacao() { return solicitacao; }
    public void setSolicitacao(SolicitacaoBeneficiario solicitacao) { this.solicitacao = solicitacao; }
    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }
    public String getNomeSalvo() { return nomeSalvo; }
    public void setNomeSalvo(String nomeSalvo) { this.nomeSalvo = nomeSalvo; }
    public String getCaminho() { return caminho; }
    public void setCaminho(String caminho) { this.caminho = caminho; }
    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }
    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }
    public String getUsuarioUpload() { return usuarioUpload; }
    public void setUsuarioUpload(String usuarioUpload) { this.usuarioUpload = usuarioUpload; }
}
