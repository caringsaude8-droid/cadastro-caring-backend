package com.caring.cadastro.operadora.domain.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SGI_CAD_BENEFICIARIO")
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGI_CAD_BENEFICIARIO_ID_SEQ")
    @SequenceGenerator(name = "SGI_CAD_BENEFICIARIO_ID_SEQ", sequenceName = "SGI_CAD_BENEFICIARIO_ID_SEQ", allocationSize = 1)
    @Column(name = "BEN_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BEN_EMP_ID")
    private Empresa empresa;

    @Column(name = "BEN_TIPO_MOTIVO")
    private String benTipoMotivo;
    @Column(name = "BEN_COD_UNIMED_SEG")
    private String benCodUnimedSeg;
    @Column(name = "BEN_RELACAO_DEP")
    private String benRelacaoDep;
    @Column(name = "BEN_DTA_NASC")
    private Date benDtaNasc;
    @Column(name = "BEN_SEXO")
    private String benSexo;
    @Column(name = "BEN_EST_CIVIL")
    private String benEstCivil;
    @Column(name = "BEN_DTA_INCLUSAO")
    private Date benDtaInclusao;
    @Column(name = "BEN_DTA_EXCLUSAO")
    private Date benDtaExclusao;
    @Column(name = "BEN_PLANO_PROD")
    private String benPlanoProd;
    @Column(name = "BEN_NOME_SEGURADO")
    private String benNomeSegurado;
    @Column(name = "BEN_CPF")
    private String benCpf;
    @Column(name = "BEN_CIDADE")
    private String benCidade;
    @Column(name = "BEN_UF")
    private String benUf;
    @Column(name = "BEN_ADMISSAO")
    private Date benAdmissao;
    @Column(name = "BEN_NOME_DA_MAE")
    private String benNomeDaMae;
    @Column(name = "BEN_ENDERECO")
    private String benEndereco;
    @Column(name = "BEN_COMPLEMENTO")
    private String benComplemento;
    @Column(name = "BEN_BAIRRO")
    private String benBairro;
    @Column(name = "BEN_CEP")
    private String benCep;
    @Column(name = "BEN_MATRICULA")
    private String benMatricula;
    @Column(name = "BEN_DDD_CEL")
    private String benDddCel;
    @Column(name = "BEN_EMAIL")
    private String benEmail;
    @Column(name = "BEN_DATA_CASAMENTO")
    private Date benDataCasamento;
    @Column(name = "BEN_INDIC_PES_TRANS")
    private String benIndicPesTrans;
    @Column(name = "BEN_NOME_SOCIAL")
    private String benNomeSocial;
    @Column(name = "BEN_IDENT_GENERO")
    private String benIdentGenero;
    @ManyToOne
    @JoinColumn(name = "BEN_TITULAR_ID")
    private Beneficiario titular;

    @Column(name = "BEN_COD_CARTAO")
    private String benCodCartao;
    @Column(name = "BEN_MOTIVO_EXCLUSAO")
    private String benMotivoExclusao;
    @Column(name = "BEN_STATUS")
    private String benStatus;
    @Column(name = "BEN_NUMERO")
    private String benNumero;

    // Getters e setters gerados para todos os campos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public String getBenTipoMotivo() { return benTipoMotivo; }
    public void setBenTipoMotivo(String benTipoMotivo) { this.benTipoMotivo = benTipoMotivo; }
    public String getBenCodUnimedSeg() { return benCodUnimedSeg; }
    public void setBenCodUnimedSeg(String benCodUnimedSeg) { this.benCodUnimedSeg = benCodUnimedSeg; }
    public String getBenRelacaoDep() { return benRelacaoDep; }
    public void setBenRelacaoDep(String benRelacaoDep) { this.benRelacaoDep = benRelacaoDep; }
    public Date getBenDtaNasc() { return benDtaNasc; }
    public void setBenDtaNasc(Date benDtaNasc) { this.benDtaNasc = benDtaNasc; }
    public String getBenSexo() { return benSexo; }
    public void setBenSexo(String benSexo) { this.benSexo = benSexo; }
    public String getBenEstCivil() { return benEstCivil; }
    public void setBenEstCivil(String benEstCivil) { this.benEstCivil = benEstCivil; }
    public Date getBenDtaInclusao() { return benDtaInclusao; }
    public void setBenDtaInclusao(Date benDtaInclusao) { this.benDtaInclusao = benDtaInclusao; }
    public Date getBenDtaExclusao() { return benDtaExclusao; }
    public void setBenDtaExclusao(Date benDtaExclusao) { this.benDtaExclusao = benDtaExclusao; }
    public String getBenPlanoProd() { return benPlanoProd; }
    public void setBenPlanoProd(String benPlanoProd) { this.benPlanoProd = benPlanoProd; }
    public String getBenNomeSegurado() { return benNomeSegurado; }
    public void setBenNomeSegurado(String benNomeSegurado) { this.benNomeSegurado = benNomeSegurado; }
    public String getBenCpf() { return benCpf; }
    public void setBenCpf(String benCpf) { this.benCpf = benCpf; }
    public String getBenCidade() { return benCidade; }
    public void setBenCidade(String benCidade) { this.benCidade = benCidade; }
    public String getBenUf() { return benUf; }
    public void setBenUf(String benUf) { this.benUf = benUf; }
    public Date getBenAdmissao() { return benAdmissao; }
    public void setBenAdmissao(Date benAdmissao) { this.benAdmissao = benAdmissao; }
    public String getBenNomeDaMae() { return benNomeDaMae; }
    public void setBenNomeDaMae(String benNomeDaMae) { this.benNomeDaMae = benNomeDaMae; }
    public String getBenEndereco() { return benEndereco; }
    public void setBenEndereco(String benEndereco) { this.benEndereco = benEndereco; }
    public String getBenComplemento() { return benComplemento; }
    public void setBenComplemento(String benComplemento) { this.benComplemento = benComplemento; }
    public String getBenBairro() { return benBairro; }
    public void setBenBairro(String benBairro) { this.benBairro = benBairro; }
    public String getBenCep() { return benCep; }
    public void setBenCep(String benCep) { this.benCep = benCep; }
    public String getBenMatricula() { return benMatricula; }
    public void setBenMatricula(String benMatricula) { this.benMatricula = benMatricula; }
    public String getBenDddCel() { return benDddCel; }
    public void setBenDddCel(String benDddCel) { this.benDddCel = benDddCel; }
    public String getBenEmail() { return benEmail; }
    public void setBenEmail(String benEmail) { this.benEmail = benEmail; }
    public Date getBenDataCasamento() { return benDataCasamento; }
    public void setBenDataCasamento(Date benDataCasamento) { this.benDataCasamento = benDataCasamento; }
    public String getBenIndicPesTrans() { return benIndicPesTrans; }
    public void setBenIndicPesTrans(String benIndicPesTrans) { this.benIndicPesTrans = benIndicPesTrans; }
    public String getBenNomeSocial() { return benNomeSocial; }
    public void setBenNomeSocial(String benNomeSocial) { this.benNomeSocial = benNomeSocial; }
    public String getBenIdentGenero() { return benIdentGenero; }
    public void setBenIdentGenero(String benIdentGenero) { this.benIdentGenero = benIdentGenero; }
    public Beneficiario getTitular() { return titular; }
    public void setTitular(Beneficiario titular) { this.titular = titular; }
    public String getBenCodCartao() { return benCodCartao; }
    public void setBenCodCartao(String benCodCartao) { this.benCodCartao = benCodCartao; }
    public String getBenMotivoExclusao() { return benMotivoExclusao; }
    public void setBenMotivoExclusao(String benMotivoExclusao) { this.benMotivoExclusao = benMotivoExclusao; }
    public String getBenStatus() { return benStatus; }
    public void setBenStatus(String benStatus) { this.benStatus = benStatus; }
    public String getBenNumero() { return benNumero; }
    public void setBenNumero(String benNumero) { this.benNumero = benNumero; }
}
