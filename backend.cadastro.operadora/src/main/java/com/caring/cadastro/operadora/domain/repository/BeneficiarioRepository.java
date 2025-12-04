package com.caring.cadastro.operadora.domain.repository;

import com.caring.cadastro.operadora.domain.entity.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {
}

