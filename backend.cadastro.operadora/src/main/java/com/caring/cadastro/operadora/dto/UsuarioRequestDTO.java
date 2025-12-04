package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.enums.PerfilUsuario;

public class UsuarioRequestDTO {
    public String cpf;
    public String nome;
    public String email;
    public String senha;
    public PerfilUsuario perfil; // ADMIN ou USER
    public String telefone;
    public Boolean status;
    public Long empresaId;
}