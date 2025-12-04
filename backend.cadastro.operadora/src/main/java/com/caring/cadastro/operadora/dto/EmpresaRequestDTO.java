package com.caring.cadastro.operadora.dto;

public class EmpresaRequestDTO {
    private String nome;
    private String cnpj;
    private String cidade;
    private String uf;
    private String email;
    private String telefone;
    private String codigoEmpresa;
    private String numeroEmpresa;

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

