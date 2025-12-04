package com.caring.cadastro.operadora.domain.repository;

import com.caring.cadastro.operadora.domain.entity.SolicitacaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SolicitacaoHistoricoRepository extends JpaRepository<SolicitacaoHistorico, Long> {

    // Buscar histórico de uma solicitação específica
    List<SolicitacaoHistorico> findBySolicitacaoIdOrderByDataOperacaoDesc(Long solicitacaoId);

    // Buscar histórico por usuário
    List<SolicitacaoHistorico> findByUsuarioIdOrderByDataOperacaoDesc(Long usuarioId);

    // Buscar operações em um período
    @Query("SELECT h FROM SolicitacaoHistorico h WHERE h.dataOperacao BETWEEN :inicio AND :fim ORDER BY h.dataOperacao DESC")
    List<SolicitacaoHistorico> findByPeriodo(@Param("inicio") Date inicio, @Param("fim") Date fim);

    // Buscar mudanças de status específico
    @Query("SELECT h FROM SolicitacaoHistorico h WHERE h.statusAnterior = :statusAnterior AND h.statusNovo = :statusNovo ORDER BY h.dataOperacao DESC")
    List<SolicitacaoHistorico> findByMudancaStatus(@Param("statusAnterior") String statusAnterior, @Param("statusNovo") String statusNovo);

    // Contar operações por usuário
    @Query("SELECT h.usuarioNome, COUNT(h) FROM SolicitacaoHistorico h GROUP BY h.usuarioNome ORDER BY COUNT(h) DESC")
    List<Object[]> contarOperacoesPorUsuario();
}
