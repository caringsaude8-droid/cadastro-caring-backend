package com.caring.cadastro.operadora.domain.repository;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.StatusSolicitacao;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoBeneficiarioRepository extends JpaRepository<SolicitacaoBeneficiario, Long> {

    List<SolicitacaoBeneficiario> findByStatusOrderByDataSolicitacaoDesc(StatusSolicitacao status);

    List<SolicitacaoBeneficiario> findByUsuarioSolicitanteIdAndStatusOrderByDataSolicitacaoDesc(Long usuarioId, StatusSolicitacao status);

    @Query("SELECT s FROM SolicitacaoBeneficiario s WHERE s.beneficiario.id = :beneficiarioId AND s.status = 'PENDENTE'")
    List<SolicitacaoBeneficiario> findSolicitacoesPendentesPorBeneficiario(@Param("beneficiarioId") Long beneficiarioId);

    @Query("SELECT COUNT(s) > 0 FROM SolicitacaoBeneficiario s WHERE s.beneficiario.id = :beneficiarioId AND s.status = 'PENDENTE'")
    boolean existeSolicitacaoPendentePorBeneficiario(@Param("beneficiarioId") Long beneficiarioId);

    // Query nativa para buscar por empresa através do usuário
    @Query(value = "SELECT s.* FROM SGI_SOLICITACOES_BENEFICIARIO s " +
           "JOIN SGI_USUARIOS u ON s.SOL_USUARIO_SOLICITANTE_ID = u.USU_ID " +
           "WHERE u.CAD_EMP_ID = :empresaId AND s.SOL_STATUS = :status " +
           "ORDER BY s.SOL_DATA_SOLICITACAO DESC", nativeQuery = true)
    List<SolicitacaoBeneficiario> findByEmpresaViaUsuarioAndStatus(@Param("empresaId") Long empresaId, @Param("status") String status);

    List<SolicitacaoBeneficiario> findByTipoAndStatusOrderByDataSolicitacaoDesc(TipoMovimentacao tipo, StatusSolicitacao status);

    List<SolicitacaoBeneficiario> findByEmpresaId(Long empresaId);
}


