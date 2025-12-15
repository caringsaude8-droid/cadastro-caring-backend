package com.caring.cadastro.operadora.controller;

import com.caring.cadastro.operadora.dto.ProcessarSolicitacaoDTO;
import com.caring.cadastro.operadora.dto.SolicitacaoRequestDTO;
import com.caring.cadastro.operadora.dto.SolicitacaoResponseDTO;
import com.caring.cadastro.operadora.dto.HistoricoSolicitacaoDTO;
import com.caring.cadastro.operadora.dto.AtualizacaoSolicitacaoDTO;
import com.caring.cadastro.operadora.service.SolicitacaoBeneficiarioService;
import com.caring.cadastro.operadora.security.JwtUtil;
import com.caring.cadastro.operadora.domain.entity.SolicitacaoBeneficiario;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cadastro/v1/solicitacoes")
public class SolicitacaoBeneficiarioController {

    private final SolicitacaoBeneficiarioService solicitacaoService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SolicitacaoBeneficiarioController(SolicitacaoBeneficiarioService solicitacaoService, JwtUtil jwtUtil) {
        this.solicitacaoService = solicitacaoService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criarSolicitacao(
            @RequestBody SolicitacaoRequestDTO dto,
            Authentication authentication) {

        System.out.println("[DEBUG] DTO recebido no controller: " + dto);

        Long usuarioId = extractUserIdFromAuth(authentication);
        String usuarioNome = extractUserNameFromAuth(authentication);

        SolicitacaoResponseDTO response = solicitacaoService.criarSolicitacao(dto, usuarioId, usuarioNome);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarPendentes(
            @RequestParam(required = false) Long empresaId) {

        List<SolicitacaoResponseDTO> solicitacoes = solicitacaoService.listarSolicitacoesPendentes(empresaId);
        return ResponseEntity.ok(solicitacoes);
    }

    @PutMapping("/{id}/processar")
    public ResponseEntity<SolicitacaoResponseDTO> processarSolicitacao(
            @PathVariable Long id,
            @RequestBody ProcessarSolicitacaoDTO dto,
            Authentication authentication) {

        System.out.println("[LOG] Corpo do request recebido: " + dto);
        Long aprovadorId = extractUserIdFromAuth(authentication);
        String aprovadorNome = extractUserNameFromAuth(authentication);

        SolicitacaoResponseDTO response = solicitacaoService.processarSolicitacao(id, dto, aprovadorId, aprovadorNome);
        System.out.println("[LOG] Response gerado: " + response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiario/{beneficiarioId}/tem-pendente")
    public ResponseEntity<Boolean> temSolicitacaoPendente(@PathVariable Long beneficiarioId) {
        boolean temPendente = solicitacaoService.temSolicitacaoPendente(beneficiarioId);
        return ResponseEntity.ok(temPendente);
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoSolicitacaoDTO>> buscarHistorico(@PathVariable Long id) {
        List<HistoricoSolicitacaoDTO> historico = solicitacaoService.buscarHistoricoSolicitacao(id);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        SolicitacaoResponseDTO response = solicitacaoService.buscarPorId(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarTodas(@RequestParam(required = false) Long empresaId) {
        if (empresaId != null) {
            return ResponseEntity.ok(solicitacaoService.listarSolicitacoesPorEmpresa(empresaId));
        }
        return ResponseEntity.ok(solicitacaoService.listarTodasSolicitacoes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarSolicitacao(@PathVariable Long id, @RequestBody AtualizacaoSolicitacaoDTO dto) {
        solicitacaoService.atualizarSolicitacao(id, dto);
        return ResponseEntity.ok().build();
    }
    //endpoint para atualizar somente o campo dadosJson por fora do fluxo
    @PutMapping("/{id}/dados-propostos")
    public ResponseEntity<?> atualizarDadosPropostos(@PathVariable Long id, @RequestBody String dadosPropostosJson) {
        SolicitacaoBeneficiario solicitacao = solicitacaoService.buscarEntidadePorId(id);
        if (solicitacao == null) {
            return ResponseEntity.notFound().build();
        }
        solicitacao.setDadosJson(dadosPropostosJson);
        solicitacaoService.salvarSomenteDadosJson(solicitacao);
        return ResponseEntity.ok().build();
    }

    // Métodos auxiliares simples
    private Long extractUserIdFromAuth(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                return 1L; // Fallback
            }
        }
        return 1L; // Fallback
    }

    private String extractUserNameFromAuth(Authentication authentication) {
        // Tenta extrair o nome do usuário do token JWT no header Authorization
        Object details = authentication != null ? authentication.getDetails() : null;
        if (details instanceof HttpServletRequest req) {
            String header = req.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Claims claims = jwtUtil.getClaimsFromToken(token);
                Object nomeClaim = claims.get("nome");
                if (nomeClaim != null) {
                    return nomeClaim.toString();
                }
            }
        }
        // Fallback: retorna o padrão antigo
        return "Usuario-" + extractUserIdFromAuth(authentication);
    }
}
