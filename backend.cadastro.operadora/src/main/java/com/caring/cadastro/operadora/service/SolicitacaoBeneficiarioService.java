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
import org.springframework.context.ApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @Autowired
    private com.caring.cadastro.operadora.domain.repository.EmpresaRepository empresaRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    public SolicitacaoResponseDTO criarSolicitacao(SolicitacaoRequestDTO dto, Long usuarioId, String usuarioNome) {
        System.out.println("[DEBUG] dto.empresaId recebido: " + dto.empresaId);
        // Buscar beneficiário
        Beneficiario beneficiario = null;
        if (dto.beneficiarioId != null) {
            beneficiario = beneficiarioRepository.findById(dto.beneficiarioId)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado (id=" + dto.beneficiarioId + ")"));
        } else {
            // Para inclusões o beneficiarioId pode ser nulo (conforme DTO). Não chamar findById(null).
            if (dto.tipo != null && dto.tipo != SolicitacaoBeneficiario.TipoMovimentacao.INCLUSAO) {
                throw new IllegalArgumentException("beneficiarioId é obrigatório para solicitações do tipo " + dto.tipo);
            }
            // No caso de INCLUSAO, o beneficiario será criado a partir de dadosPropostos (ou tratado posteriormente).
        }

        // Verificar se já existe solicitação pendente para este beneficiário
        if (dto.beneficiarioId != null && solicitacaoRepository.existeSolicitacaoPendentePorBeneficiario(dto.beneficiarioId)) {
            throw new RuntimeException("Já existe uma solicitação pendente para este beneficiário");
        }

        // Criar solicitação
        SolicitacaoBeneficiario solicitacao = new SolicitacaoBeneficiario();
        solicitacao.setBeneficiario(beneficiario);
        solicitacao.setTipo(dto.tipo);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setMotivoExclusao(dto.motivoExclusao);
        solicitacao.setObservacoesSolicitacao(dto.observacoesSolicitacao);
        solicitacao.setUsuarioSolicitanteId(usuarioId);
        solicitacao.setUsuarioSolicitanteNome(usuarioNome);

        // Preencher beneficiarioCpf e beneficiarioNome conforme o tipo de solicitação
        if (dto.tipo == SolicitacaoBeneficiario.TipoMovimentacao.INCLUSAO) {
            // Inclusão: usar apenas o valor do DTO (corpo principal)
            solicitacao.setBeneficiarioCpf(dto.beneficiarioCpf);
            solicitacao.setBeneficiarioNome(dto.beneficiarioNome);
        } else {
            // Alteração/Exclusão: sempre usa o valor do beneficiário já cadastrado
            if (beneficiario != null) {
                solicitacao.setBeneficiarioCpf(beneficiario.getBenCpf());
                solicitacao.setBeneficiarioNome(beneficiario.getBenNomeSegurado());
            }
        }
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setMotivoExclusao(dto.motivoExclusao);
        solicitacao.setObservacoesSolicitacao(dto.observacoesSolicitacao);
        solicitacao.setUsuarioSolicitanteId(usuarioId);
        solicitacao.setUsuarioSolicitanteNome(usuarioNome);

        // Associar empresa do beneficiário, se houver, ou do DTO se enviado
        if (dto.empresaId != null) {
            com.caring.cadastro.operadora.domain.entity.Empresa empresa = empresaRepository.findById(dto.empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada (id=" + dto.empresaId + ")"));
            solicitacao.setEmpresa(empresa);
            System.out.println("[DEBUG] empresaId recebido do front: " + dto.empresaId + ", associado: " + (empresa != null ? empresa.getId() : null));
        } else if (beneficiario != null && beneficiario.getEmpresa() != null) {
            solicitacao.setEmpresa(beneficiario.getEmpresa());
        }
        System.out.println("[DEBUG] empresaId na solicitação antes do save: " + (solicitacao.getEmpresa() != null ? solicitacao.getEmpresa().getId() : null));

        // Serializar dados propostos como JSON (para inclusão/alteração)
        if (dto.dadosPropostos != null) {
            try {
                solicitacao.setDadosJson(objectMapper.writeValueAsString(dto.dadosPropostos));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar dados da solicitação", e);
            }
        }

        // Preencher campos adicionais do DTO se enviados
        if (dto.beneficiarioNome != null) {
            solicitacao.setBeneficiarioNome(dto.beneficiarioNome);
        }
        if (dto.beneficiarioCpf != null) {
            solicitacao.setBeneficiarioCpf(dto.beneficiarioCpf);
        }
        if (dto.observacoes != null) {
            solicitacao.setObservacoes(dto.observacoes);
        }
        if (dto.observacoesAprovacao != null) {
            solicitacao.setObservacoesAprovacao(dto.observacoesAprovacao);
        }

        // Garantir que beneficiarioCpf e beneficiarioNome sejam preenchidos do corpo principal ou de dadosPropostos
        // Remover busca em dadosPropostos, pois o frontend envia beneficiarioNome e beneficiarioCpf apenas no corpo principal

        System.out.println("[DEBUG] DTO recebido do controller: " + dto);
        System.out.println("[DEBUG] CPF para gravar: " + solicitacao.getBeneficiarioCpf());
        System.out.println("[DEBUG] Nome para gravar: " + solicitacao.getBeneficiarioNome());

        solicitacao = solicitacaoRepository.save(solicitacao);
        entityManager.flush();
        System.out.println("[DEBUG] empresaId na solicitação após save/flush: " + (solicitacao.getEmpresa() != null ? solicitacao.getEmpresa().getId() : null));
        return toResponseDTO(solicitacao);
    }

    public List<SolicitacaoResponseDTO> listarSolicitacoesPendentes(Long empresaId) {
        List<SolicitacaoBeneficiario> solicitacoes;

        if (empresaId != null) {
            solicitacoes = solicitacaoRepository.findByEmpresaId(empresaId);
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
        System.out.println("[LOG] Iniciando processamento da solicitação: id=" + solicitacaoId);
        System.out.println("[LOG] DTO recebido: " + (dto != null ? dto.toString() : "null"));
        SolicitacaoBeneficiario solicitacao = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        System.out.println("[LOG] Solicitacao carregada do banco: " + solicitacao.toString());
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            System.out.println("[LOG] Solicitacao já processada, status=" + solicitacao.getStatus());
            throw new RuntimeException("Solicitação já foi processada");
        }

        // Atualizar dados da aprovação
        solicitacao.setAprovadorId(aprovadorId);
        solicitacao.setAprovadorNome(aprovadorNome);
        solicitacao.setObservacoesAprovacao(dto.observacoesAprovacao);
        solicitacao.setDataAprovacao(new java.util.Date());

        // NOVO: Se vier dadosAprovacao, mesclar com dadosPropostos do dadosJson, apenas para INCLUSAO
        if ("APROVAR".equals(dto.acao)
                && dto.dadosAprovacao != null
                && solicitacao.getTipo() == com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario.TipoMovimentacao.INCLUSAO) {
            try {
                String dadosJsonOriginal = solicitacao.getDadosJson();
                System.out.println("[DEBUG] dadosJson original: " + dadosJsonOriginal);
                if (dadosJsonOriginal != null) {
                    // Tenta desserializar como Map para garantir flexibilidade
                    java.util.Map<String, Object> dadosMap = objectMapper.readValue(
                        dadosJsonOriginal, java.util.Map.class);
                    System.out.println("[DEBUG] dadosMap antes: " + dadosMap);
                    if (dto.dadosAprovacao.benCodCartao != null) {
                        dadosMap.put("benCodCartao", dto.dadosAprovacao.benCodCartao);
                    }
                    if (dto.dadosAprovacao.benCodUnimedSeg != null) {
                        dadosMap.put("benCodUnimedSeg", dto.dadosAprovacao.benCodUnimedSeg);
                    }
                    System.out.println("[DEBUG] dadosMap depois: " + dadosMap);
                    solicitacao.setDadosJson(objectMapper.writeValueAsString(dadosMap));
                    System.out.println("[LOG] Dados de aprovação mesclados no JSON raiz.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Erro ao mesclar dados de aprovação: " + e.getMessage());
                throw new RuntimeException("Erro ao mesclar dados de aprovação", e);
            }
        }

        if ("APROVAR".equals(dto.acao)) {
            solicitacao.setStatus(StatusSolicitacao.APROVADA);
            System.out.println("[LOG] Efetivando alteração para solicitação id=" + solicitacaoId);
            efetivarAlteracao(solicitacao);
        } else if ("REJEITAR".equals(dto.acao)) {
            solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        }

        System.out.println("[LOG] Solicitacao antes do save: " + solicitacao.toString());
        solicitacao = solicitacaoRepository.save(solicitacao);
        System.out.println("[LOG] Solicitacao após o save: " + solicitacao.toString());
        return toResponseDTO(solicitacao);
    }

    private void efetivarAlteracao(SolicitacaoBeneficiario solicitacao) {
        try {
            System.out.println("[DEBUG] Entrou em efetivarAlteracao, tipo=" + solicitacao.getTipo());
            switch (solicitacao.getTipo()) {
                case EXCLUSAO:
                    // Para exclusões, usar beneficiário existente
                    Beneficiario beneficiario = solicitacao.getBeneficiario();
                    beneficiario.setBenStatus("RESCINDIDO");
                    beneficiario.setBenDtaExclusao(new Date());
                    beneficiario.setBenMotivoExclusao(solicitacao.getMotivoExclusao());
                    beneficiarioRepository.save(beneficiario);

                    // NOVO: Rescindir todos os dependentes do titular
                    if (beneficiario.getId() != null && beneficiario.getEmpresa() != null) {
                        Long titularId = beneficiario.getId();
                        Long empresaId = beneficiario.getEmpresa().getId();
                        List<Beneficiario> dependentes = beneficiarioRepository.findByTitularIdAndEmpresaId(titularId, empresaId);
                        for (Beneficiario dependente : dependentes) {
                            dependente.setBenStatus("RESCINDIDO");
                            dependente.setBenDtaExclusao(new Date());
                            dependente.setBenMotivoExclusao("Rescindido automaticamente por exclusão do titular");
                            // Atualiza o tipo motivo igual ao do titular
                            dependente.setBenTipoMotivo(beneficiario.getBenTipoMotivo());
                        }
                        beneficiarioRepository.saveAll(dependentes);
                    }
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
                    System.out.println("[DEBUG] Vai criar beneficiario, dadosJson=" + solicitacao.getDadosJson());
                    if (solicitacao.getDadosJson() != null) {
                        BeneficiarioRequestDTO novosDados = null;
                        try {
                            novosDados = objectMapper.readValue(
                                solicitacao.getDadosJson(),
                                BeneficiarioRequestDTO.class
                            );
                        } catch (Exception e) {
                            System.out.println("[ERROR] Falha ao desserializar dadosJson: " + e.getMessage());
                            throw new RuntimeException("Erro ao desserializar dadosJson", e);
                        }
                        if (novosDados == null) {
                            System.out.println("[ERROR] BeneficiarioRequestDTO desserializado é nulo! JSON: " + solicitacao.getDadosJson());
                            throw new RuntimeException("Erro ao efetivar solicitação: dadosJson não pôde ser convertido para BeneficiarioRequestDTO");
                        }
                        System.out.println("[DEBUG] BeneficiarioRequestDTO desserializado: " + novosDados);
                        beneficiarioService.criarBeneficiario(novosDados);
                    }
                    break;
            }
            System.out.println("[DEBUG] Saiu de efetivarAlteracao");
            solicitacao.setDataEfetivacao(new Date());
        } catch (Exception e) {
            System.out.println("[ERROR] Erro ao efetivar solicitação: " + e.getMessage());
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
        dto.dadosJson = solicitacao.getDadosJson();
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

    public void atualizarSolicitacao(Long id, AtualizacaoSolicitacaoDTO dto) {
        SolicitacaoBeneficiario solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        if (solicitacao.getStatus() != SolicitacaoBeneficiario.StatusSolicitacao.REJEITADA) {
            throw new RuntimeException("Só é possível editar solicitações rejeitadas");
        }
        if (dto.observacoesSolicitacao != null) {
            solicitacao.setObservacoesSolicitacao(dto.observacoesSolicitacao);
        }
        if (dto.dadosPropostos != null) {
            try {
                solicitacao.setDadosJson(objectMapper.writeValueAsString(dto.dadosPropostos));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao serializar dadosPropostos", e);
            }
        }
        solicitacao.setStatus(SolicitacaoBeneficiario.StatusSolicitacao.PENDENTE); // volta para análise
        solicitacaoRepository.save(solicitacao);
    }

    public SolicitacaoBeneficiario buscarEntidadePorId(Long id) {
        return solicitacaoRepository.findById(id).orElse(null);
    }

    public void salvarSomenteDadosJson(SolicitacaoBeneficiario solicitacao) {
        solicitacaoRepository.save(solicitacao);
    }
}
