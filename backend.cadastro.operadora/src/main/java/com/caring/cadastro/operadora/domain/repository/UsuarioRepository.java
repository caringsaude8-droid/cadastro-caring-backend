package com.caring.cadastro.operadora.domain.repository;

import com.caring.cadastro.operadora.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCpf(String cpf);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}