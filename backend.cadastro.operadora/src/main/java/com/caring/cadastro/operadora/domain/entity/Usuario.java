package com.caring.cadastro.operadora.domain.entity;

import com.caring.cadastro.operadora.domain.converter.PerfilUsuarioConverter;
import com.caring.cadastro.operadora.domain.enums.PerfilUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "SGI_USUARIOS")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USU_ID")
    @SequenceGenerator(name = "USU_ID", sequenceName = "USU_ID", allocationSize = 1)
    @Column(name = "USU_ID")
    private Long id;

    @Column(name = "USU_CPF", nullable = false, unique = true)
    private String cpf;

    @Column(name = "USU_NOME", nullable = false)
    private String nome;

    @Column(name = "USU_EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "USU_SENHA", nullable = false)
    private String senha;

    @Column(name = "USU_STATUS", nullable = false)
    private Boolean status = true;

    @Column(name = "USU_TELEFONE")
    private String telefone;

    @Convert(converter = PerfilUsuarioConverter.class)
    @Column(name = "PERFIL_ID", nullable = false)
    private PerfilUsuario perfil;

    @ManyToOne
    @JoinColumn(name = "CAD_EMP_ID")
    private Empresa empresa;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}