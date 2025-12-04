package com.caring.cadastro.operadora.dto;

import com.caring.cadastro.operadora.domain.enums.PerfilUsuario;

public class UsuarioResponseDTO {
    public Long id;
    public String nome;
    public String email;
    public Boolean status;
    public PerfilUsuario perfil; // ADMIN ou USER
    public String telefone;
    public Long empresaId;
}
