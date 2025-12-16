package com.caring.cadastro.operadora.domain.repository;

import com.caring.cadastro.operadora.domain.entity.BenAnexo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BenAnexoRepository extends JpaRepository<BenAnexo, Long> {
    List<BenAnexo> findBySolicitacaoId(Long solicitacaoId);
}

