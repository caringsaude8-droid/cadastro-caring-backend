package com.caring.cadastro.operadora.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.caring.cadastro.operadora.domain.entity.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Métodos customizados podem ser adicionados aqui
}
