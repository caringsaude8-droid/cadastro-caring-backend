package com.caring.cadastro.operadora.domain.converter;

import com.caring.cadastro.operadora.domain.enums.PerfilUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PerfilUsuarioConverter implements AttributeConverter<PerfilUsuario, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PerfilUsuario perfil) {
        if (perfil == null) return null;
        switch (perfil) {
            case USER: return 1;
            case ADMIN: return 2;
            case GESTOR: return 3;
            default: throw new IllegalArgumentException("PerfilUsuario desconhecido: " + perfil);
        }
    }

    @Override
    public PerfilUsuario convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        switch (dbData) {
            case 1: return PerfilUsuario.USER;
            case 2: return PerfilUsuario.ADMIN;
            case 3: return PerfilUsuario.GESTOR;
            default: throw new IllegalArgumentException("PerfilUsuario desconhecido no banco: " + dbData);
        }
    }
}
