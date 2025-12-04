package com.caring.cadastro.operadora.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SGI_CAD_EMPRESAS")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SGI_CAD_EMPRESAS_ID_SEQ")
    @SequenceGenerator(name = "SGI_CAD_EMPRESAS_ID_SEQ", sequenceName = "SGI_CAD_EMPRESAS_ID_SEQ", allocationSize = 1)
    @Column(name = "EMP_ID")
    private Long id;

    @Column(name = "EMP_NOME", nullable = false)
    private String nome;

    @Column(name = "EMP_CNPJ", nullable = false, unique = true)
    private String cnpj;

    @Column(name = "EMP_CIDADE")
    private String cidade;

    @Column(name = "EMP_UF", length = 2)
    private String uf;

    @Column(name = "EMP_EMAIL")
    private String email;

    @Column(name = "EMP_TELEFONE")
    private String telefone;

    @Column(name = "EMP_CODIGO_EMPRESA")
    private String codigoEmpresa;

    @Column(name = "EMP_NUMERO_EMPRESA")
    private String numeroEmpresa;

    public Empresa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCodigoEmpresa() { return codigoEmpresa; }
    public void setCodigoEmpresa(String codigoEmpresa) { this.codigoEmpresa = codigoEmpresa; }
    public String getNumeroEmpresa() { return numeroEmpresa; }
    public void setNumeroEmpresa(String numeroEmpresa) { this.numeroEmpresa = numeroEmpresa; }
}
