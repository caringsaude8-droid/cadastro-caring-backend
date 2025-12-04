package com.caring.cadastro.operadora.service;

import com.caring.cadastro.operadora.domain.entity.Beneficiario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.StatusSolicitacao;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoHistorico;
import com.caring.cadastro.operadora.domain.repository.BeneficiarioRepository;
import com.caring.cadastro.operadora.domain.repository.SolicitacaoBeneficiarioRepository;
import com.caring.cadastro.operadora.domain.repository.SolicitacaoHistoricoRepository;
import com.caring.cadastro.operadora.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolicitacaoBeneficiarioService {

    @Autowired
    private SolicitacaoBeneficiarioRepository solicitacaoRepository;

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private BeneficiarioService beneficiarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SolicitacaoHistoricoRepository historicoRepository;

    public SolicitacaoResponseDTO criarSolicitacao(SolicitacaoRequestDTO dto, Long usuarioId, String usuarioNome) {
        // Buscar beneficiário
        Beneficiario beneficiario = beneficiarioRepository.findById(dto.beneficiarioId)
            .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado"));

        // Verificar se já existe solicitação pendente para este beneficiário
        if (solicitacaoRepository.existeSolicitacaoPendentePorBeneficiario(dto.beneficiarioId)) {
            throw new RuntimeException("Já existe uma solicitação pendente para este beneficiário");
        }

        // Criar solicitação
        SolicitacaoBeneficiario solicitacao = new SolicitacaoBeneficiario();
        solicitacao.setBeneficiario(beneficiario);
        solicitacao.setBeneficiarioCpf(beneficiario.getBenCpf());
        solicitacao.setBeneficiarioNome(beneficiario.getBenNomeSegurado());
        solicitacao.setTipo(dto.tipo);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setMotivoExclusao(dto.motivoExclusao);
        solicitacao.setObservacoesSolicitacao(dto.observacoesSolicitacao);
        solicitacao.setUsuarioSolicitanteId(usuarioId);
        solicitacao.setUsuarioSolicitanteNome(usuarioNome);

        // Associar empresa do beneficiário, se houver, ou do DTO se enviado
        if (dto.empresaId != null) {
            // Buscar empresa pelo id
            com.caring.cadastro.operadora.domain.entity.Empresa empresa = null;
            try {
                empresa = beneficiario.getEmpresa(); // fallback
                // Buscar empresa pelo id se diferente do beneficiário
                if (beneficiario.getEmpresa() == null || !beneficiario.getEmpresa().getId().equals(dto.empresaId)) {
                    // TODO: Buscar repositório de empresa e atribuir
                    // Exemplo: empresa = empresaRepository.findById(dto.empresaId).orElse(null);
                }
            } catch (Exception e) {
                // fallback para null
            }
            if (empresa != null) {
                solicitacao.setEmpresa(empresa);
            }
        } else if (beneficiario.getEmpresa() != null) {
            solicitacao.setEmpresa(beneficiario.getEmpresa());
        }

        // Serializar dados propostos como JSON (para inclusão/alteração)
        if (dto.dadosPropostos != null) {
            try {
                solicitacao.setDadosJson(objectMapper.writeValueAsString(dto.dadosPropostos));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar dados da solicitação", e);
            }
        }

        solicitacao = solicitacaoRepository.save(solicitacao);
        return toResponseDTO(solicitacao);
    }

    public List<SolicitacaoResponseDTO> listarSolicitacoesPendentes(Long empresaId) {
        List<SolicitacaoBeneficiario> solicitacoes;

        if (empresaId != null) {
            solicitacoes = solicitacaoRepository.findByEmpresaViaUsuarioAndStatus(
                empresaId, "PENDENTE");
        } else {
            solicitacoes = solicitacaoRepository.findByStatusOrderByDataSolicitacaoDesc(
                StatusSolicitacao.PENDENTE);
        }

        // Corrigido: garantir conversão para DTO
        return solicitacoes.stream()
            .map(SolicitacaoBeneficiarioService::toResponseDTO)
            .collect(Collectors.toList());
    }

    public List<SolicitacaoResponseDTO> listarTodasSolicitacoes() {
        List<SolicitacaoBeneficiario> solicitacoes = solicitacaoRepository.findAll();
        return solicitacoes.stream()
            .map(SolicitacaoBeneficiarioService::toResponseDTO)
            .collect(Collectors.toList());
    }

    public SolicitacaoResponseDTO processarSolicitacao(Long solicitacaoId, ProcessarSolicitacaoDTO dto,
                                                      Long aprovadorId, String aprovadorNome) {
        SolicitacaoBeneficiario solicitacao = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Solicitação já foi processada");
        }

        // Atualizar dados da aprovação
        solicitacao.setAprovadorId(aprovadorId);
        solicitacao.setAprovadorNome(aprovadorNome);
        solicitacao.setObservacoesAprovacao(dto.observacoesAprovacao);
        solicitacao.setDataAprovacao(new java.util.Date());

        // NOVO: Se vier dadosAprovacao, sobrescreve ou complementa o dadosJson
        if (dto.dadosAprovacao != null) {
            try {
                solicitacao.setDadosJson(objectMapper.writeValueAsString(dto.dadosAprovacao));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar dados da aprovação", e);
            }
        }

        if ("APROVAR".equals(dto.acao)) {
            solicitacao.setStatus(StatusSolicitacao.APROVADA);
            // Efetivar a alteração no beneficiário
            efetivarAlteracao(solicitacao);
        } else if ("REJEITAR".equals(dto.acao)) {
            solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        }

        solicitacao = solicitacaoRepository.save(solicitacao);
        return toResponseDTO(solicitacao);
    }

    private void efetivarAlteracao(SolicitacaoBeneficiario solicitacao) {
        try {
            switch (solicitacao.getTipo()) {
                case EXCLUSAO:
                    // Para exclusões, usar beneficiário existente
                    Beneficiario beneficiario = solicitacao.getBeneficiario();
                    beneficiario.setBenStatus("RESCINDIDO");
                    beneficiario.setBenDtaExclusao(new Date());
                    beneficiario.setBenMotivoExclusao(solicitacao.getMotivoExclusao());
                    beneficiarioRepository.save(beneficiario);
                    break;

                case ALTERACAO:
                    // Desserializar dados JSON e aplicar alteração
                    if (solicitacao.getDadosJson() != null) {
                        BeneficiarioRequestDTO dadosAlteracao = objectMapper.readValue(
                            solicitacao.getDadosJson(),
                            BeneficiarioRequestDTO.class
                        );
                        beneficiarioService.atualizarBeneficiario(
                            solicitacao.getBeneficiario().getId(),
                            dadosAlteracao
                        );
                    }
                    break;

                case INCLUSAO:
                    // Desserializar dados JSON e criar novo beneficiário
                    if (solicitacao.getDadosJson() != null) {
                        BeneficiarioRequestDTO novosDados = objectMapper.readValue(
                            solicitacao.getDadosJson(),
                            BeneficiarioRequestDTO.class
                        );
                        beneficiarioService.criarBeneficiario(novosDados);
                    }
                    break;
            }

            solicitacao.setDataEfetivacao(new Date());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao efetivar solicitação", e);
        }
    }

    public boolean temSolicitacaoPendente(Long beneficiarioId) {
        return solicitacaoRepository.existeSolicitacaoPendentePorBeneficiario(beneficiarioId);
    }

    public List<HistoricoSolicitacaoDTO> buscarHistoricoSolicitacao(Long solicitacaoId) {
        List<SolicitacaoHistorico> historicos = historicoRepository.findBySolicitacaoIdOrderByDataOperacaoDesc(solicitacaoId);

        return historicos.stream()
            .map(this::toHistoricoDTO)
            .collect(Collectors.toList());
    }

    private HistoricoSolicitacaoDTO toHistoricoDTO(SolicitacaoHistorico historico) {
        HistoricoSolicitacaoDTO dto = new HistoricoSolicitacaoDTO();
        dto.id = historico.getId();
        dto.solicitacaoId = historico.getSolicitacaoId();
        dto.operacao = historico.getOperacao();
        dto.statusAnterior = historico.getStatusAnterior();
        dto.statusNovo = historico.getStatusNovo();
        dto.campoAlterado = historico.getCampoAlterado();
        dto.valorAnterior = historico.getValorAnterior();
        dto.valorNovo = historico.getValorNovo();
        dto.usuarioNome = historico.getUsuarioNome();
        dto.dataOperacao = historico.getDataOperacao();
        dto.observacoes = historico.getObservacoes();
        dto.ipOrigem = historico.getIpOrigem();

        // Campos calculados
        dto.descricaoOperacao = gerarDescricaoOperacao(historico);
        dto.tipoOperacaoDescricao = getTipoOperacaoDescricao(historico.getOperacao());

        return dto;
    }

    private String gerarDescricaoOperacao(SolicitacaoHistorico historico) {
        switch (historico.getOperacao()) {
            case INSERT:
                return "Solicitação criada por " + historico.getUsuarioNome();
            case UPDATE:
                if ("SOL_STATUS".equals(historico.getCampoAlterado())) {
                    return String.format("Status alterado de '%s' para '%s' por %s",
                        historico.getStatusAnterior(),
                        historico.getStatusNovo(),
                        historico.getUsuarioNome());
                } else {
                    return String.format("Campo '%s' alterado por %s",
                        historico.getCampoAlterado(),
                        historico.getUsuarioNome());
                }
            case DELETE:
                return "Solicitação excluída por " + historico.getUsuarioNome();
            default:
                return "Operação realizada por " + historico.getUsuarioNome();
        }
    }

    private String getTipoOperacaoDescricao(SolicitacaoHistorico.TipoOperacao operacao) {
        switch (operacao) {
            case INSERT: return "Criação";
            case UPDATE: return "Atualização";
            case DELETE: return "Exclusão";
            default: return "Operação";
        }
    }

    public static SolicitacaoResponseDTO toResponseDTO(SolicitacaoBeneficiario solicitacao) {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.id = solicitacao.getId();
        dto.numeroSolicitacao = solicitacao.getNumeroSolicitacao();
        dto.beneficiarioId = solicitacao.getBeneficiario() != null ? solicitacao.getBeneficiario().getId() : null;
        dto.beneficiarioCpf = solicitacao.getBeneficiarioCpf();
        dto.beneficiarioNome = solicitacao.getBeneficiarioNome();
        dto.tipo = solicitacao.getTipo();
        dto.status = solicitacao.getStatus();
        dto.motivoExclusao = solicitacao.getMotivoExclusao();
        dto.dataSolicitacao = solicitacao.getDataSolicitacao();
        dto.dataAprovacao = solicitacao.getDataAprovacao();
        dto.dataEfetivacao = solicitacao.getDataEfetivacao();
        dto.usuarioSolicitanteNome = solicitacao.getUsuarioSolicitanteNome();
        dto.aprovadorNome = solicitacao.getAprovadorNome();
        dto.observacoesSolicitacao = solicitacao.getObservacoesSolicitacao();
        dto.observacoesAprovacao = solicitacao.getObservacoesAprovacao();
        dto.empresaId = solicitacao.getEmpresa() != null ? solicitacao.getEmpresa().getId() : null;
        return dto;
    }

    public SolicitacaoResponseDTO buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
            .map(SolicitacaoBeneficiarioService::toResponseDTO)
            .orElse(null);
    }

    public List<SolicitacaoResponseDTO> listarSolicitacoesPorEmpresa(Long empresaId) {
        List<SolicitacaoBeneficiario> solicitacoes = solicitacaoRepository.findByEmpresaId(empresaId);
        return solicitacoes.stream()
            .map(SolicitacaoBeneficiarioService::toResponseDTO)
            .collect(Collectors.toList());
    }
}
