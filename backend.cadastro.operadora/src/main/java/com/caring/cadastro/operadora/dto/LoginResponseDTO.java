package com.caring.cadastro.operadora.dto;

public class LoginResponseDTO {
    public String token;
    public String refreshToken;
    public UsuarioLoginDTO user;

    public static class UsuarioLoginDTO {
        public Long id;
        public String nome;
        public String email;
        public Boolean status;
        public String perfil;
        public Long empresaId;
    }
}
