package com.caring.cadastro.operadora.controller;

import com.caring.cadastro.operadora.dto.LoginRequestDTO;
import com.caring.cadastro.operadora.dto.LoginResponseDTO;
import com.caring.cadastro.operadora.dto.UsuarioRequestDTO;
import com.caring.cadastro.operadora.dto.UsuarioResponseDTO;
import com.caring.cadastro.operadora.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cadastro/v1/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO response = usuarioService.criarUsuario(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuario(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO usuario = usuarioService.atualizarUsuario(id, request);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        logger.info("Recebida requisição de login para email: {}", request.getEmail());
        // Autenticação simples: buscar usuário pelo e-mail e senha
        UsuarioRequestDTO usuarioRequest = new UsuarioRequestDTO();
        usuarioRequest.email = request.getEmail();
        usuarioRequest.senha = request.getSenha();
        UsuarioResponseDTO usuario = usuarioService.autenticarUsuario(usuarioRequest);
        if (usuario == null) {
            logger.warn("Falha no login para email: {}", request.getEmail());
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
        // Gerar resposta completa de login com token, refreshToken e dados do usuário
        LoginResponseDTO loginResponse = usuarioService.gerarLoginResponse(usuario);
        logger.info("Login bem-sucedido para email: {}", request.getEmail());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody java.util.Map<String, String> refreshRequest) {
        logger.info("Recebida requisição de refresh token");

        try {
            String refreshToken = refreshRequest.get("refreshToken");

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                logger.warn("Refresh token não fornecido");
                return ResponseEntity.status(400).body("Token de refresh não fornecido");
            }

            // Validar refresh token
            LoginResponseDTO refreshResponse = usuarioService.refreshToken(refreshToken);

            if (refreshResponse == null) {
                logger.warn("Refresh token inválido ou expirado");
                return ResponseEntity.status(401).body("Token de refresh inválido ou expirado");
            }

            logger.info("Refresh token executado com sucesso");
            return ResponseEntity.ok(refreshResponse);

        } catch (Exception e) {
            logger.error("Erro no refresh token: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erro interno ao processar refresh token");
        }
    }
}
