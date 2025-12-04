package com.caring.cadastro.operadora.service;

import com.caring.cadastro.operadora.domain.entity.Empresa;
import com.caring.cadastro.operadora.domain.entity.Usuario;
import com.caring.cadastro.operadora.domain.enums.PerfilUsuario;
import com.caring.cadastro.operadora.domain.repository.EmpresaRepository;
import com.caring.cadastro.operadora.domain.repository.UsuarioRepository;
import com.caring.cadastro.operadora.dto.LoginResponseDTO;
import com.caring.cadastro.operadora.dto.UsuarioRequestDTO;
import com.caring.cadastro.operadora.dto.UsuarioResponseDTO;
import com.caring.cadastro.operadora.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO requestDTO) {
        // Verifica se já existe usuário com o mesmo CPF
        if (usuarioRepository.existsByCpf(requestDTO.cpf)) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Verifica se já existe usuário com o mesmo email
        if (usuarioRepository.existsByEmail(requestDTO.email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Define o perfil do usuário, padrão é USER
        PerfilUsuario perfil = requestDTO.perfil != null ? requestDTO.perfil : PerfilUsuario.USER;

        Usuario usuario = new Usuario();
        usuario.setCpf(requestDTO.cpf);
        usuario.setNome(requestDTO.nome);
        usuario.setEmail(requestDTO.email);
        usuario.setSenha(passwordEncoder.encode(requestDTO.senha)); // Agora criptografa a senha
        usuario.setPerfil(perfil);
        usuario.setStatus(requestDTO.status != null ? requestDTO.status : true);
        usuario.setTelefone(requestDTO.telefone);

        if (requestDTO.empresaId != null) {
            Empresa empresa = empresaRepository.findById(requestDTO.empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
            usuario.setEmpresa(empresa);
        } else {
            usuario.setEmpresa(null);
        }

        usuario = usuarioRepository.save(usuario);

        return converterParaDTO(usuario);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioResponseDTO> dtos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            dtos.add(converterParaDTO(usuario));
        }
        return dtos;
    }

    public UsuarioResponseDTO buscarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return converterParaDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica se o email já existe para outro usuário
        if (!usuario.getEmail().equals(requestDTO.email) &&
                usuarioRepository.existsByEmail(requestDTO.email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Define o perfil do usuário, se não informado usa o existente
        PerfilUsuario perfil = requestDTO.perfil != null ? requestDTO.perfil : usuario.getPerfil();

        usuario.setNome(requestDTO.nome);
        usuario.setEmail(requestDTO.email);
        if (requestDTO.senha != null && !requestDTO.senha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(requestDTO.senha)); // Agora criptografa a senha ao atualizar
        }
        usuario.setPerfil(perfil);
        usuario.setTelefone(requestDTO.telefone);
        if (requestDTO.status != null) {
            usuario.setStatus(requestDTO.status);
        }

        if (requestDTO.empresaId != null) {
            Empresa empresa = empresaRepository.findById(requestDTO.empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
            usuario.setEmpresa(empresa);
        } else {
            usuario.setEmpresa(null);
        }

        usuario = usuarioRepository.save(usuario);

        return converterParaDTO(usuario);
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO autenticarUsuario(UsuarioRequestDTO requestDTO) {
        logger.info("Tentando autenticar usuário com email: {}", requestDTO.email);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(requestDTO.email);
        if (usuarioOpt.isPresent()) {
            logger.info("Usuário encontrado para o email: {}", requestDTO.email);
            boolean senhaOk = passwordEncoder.matches(requestDTO.senha, usuarioOpt.get().getSenha());
            logger.info("Senha confere? {}", senhaOk);
            if (senhaOk) {
                return converterParaDTO(usuarioOpt.get());
            }
        } else {
            logger.warn("Nenhum usuário encontrado para o email: {}", requestDTO.email);
        }
        return null;
    }

    public String gerarToken(UsuarioResponseDTO usuario) {
        // Adiciona todas as claims necessárias
        Map<String, Object> claims = new HashMap<>();
        claims.put("nome", usuario.nome);
        claims.put("id", usuario.id);
        claims.put("email", usuario.email);
        claims.put("perfil", usuario.perfil != null ? usuario.perfil.name() : "USER");

        // Incluir empresaId apenas se existir (pode ser null para usuários ADMIN)
        if (usuario.empresaId != null) {
            claims.put("empresaId", usuario.empresaId);
        }

        // Define o subject como o ID do usuário (string)
        return jwtUtil.generateToken(String.valueOf(usuario.id), claims);
    }

    public String gerarRefreshToken(UsuarioResponseDTO usuario) {
        // Adiciona todas as claims necessárias para o refresh token
        Map<String, Object> claims = new HashMap<>();
        claims.put("nome", usuario.nome);
        claims.put("id", usuario.id);
        claims.put("email", usuario.email);
        claims.put("perfil", usuario.perfil != null ? usuario.perfil.name() : "USER");

        // Incluir empresaId apenas se existir (pode ser null para usuários ADMIN)
        if (usuario.empresaId != null) {
            claims.put("empresaId", usuario.empresaId);
        }

        // Refresh token com expiração maior (30 dias em vez de 1 hora)
        return jwtUtil.generateRefreshToken(String.valueOf(usuario.id), claims);
    }

    public LoginResponseDTO gerarLoginResponse(UsuarioResponseDTO usuario) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.token = gerarToken(usuario);
        response.refreshToken = gerarRefreshToken(usuario);

        // Criar objeto user para a resposta
        LoginResponseDTO.UsuarioLoginDTO userLogin = new LoginResponseDTO.UsuarioLoginDTO();
        userLogin.id = usuario.id;
        userLogin.nome = usuario.nome;
        userLogin.email = usuario.email;
        userLogin.status = usuario.status;
        userLogin.perfil = usuario.perfil != null ? usuario.perfil.name() : "USER";
        userLogin.empresaId = usuario.empresaId;
        // Removido userLogin.perfilId e outros campos não usados
        response.user = userLogin;
        return response;
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        try {
            // Validar o refresh token
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                logger.warn("Refresh token inválido");
                return null;
            }

            // Extrair dados do refresh token
            String userId = jwtUtil.getSubject(refreshToken);
            if (userId == null) {
                logger.warn("Subject não encontrado no refresh token");
                return null;
            }

            // Buscar usuário no banco
            Usuario usuario = usuarioRepository.findById(Long.valueOf(userId)).orElse(null);
            if (usuario == null) {
                logger.warn("Usuário não encontrado no banco: {}", userId);
                return null;
            }

            // Verificar se o usuário ainda está ativo
            if (usuario.getStatus() == null || !usuario.getStatus()) {
                logger.warn("Usuário inativo tentando renovar token: {}", userId);
                return null;
            }

            // Converter para DTO garantindo que o perfil seja definido
            UsuarioResponseDTO usuarioDto = converterParaDTO(usuario);

            // Gerar nova resposta de login com novos tokens
            LoginResponseDTO response = gerarLoginResponse(usuarioDto);
            logger.info("Token renovado com sucesso para usuário: {} (perfil: {})",
                       usuarioDto.nome, usuarioDto.perfil);

            return response;

        } catch (Exception e) {
            logger.error("Erro no refresh token: {}", e.getMessage(), e);
            return null;
        }
    }

    private Integer getPerfilId(PerfilUsuario perfil) {
        if (perfil == null) return 1; // Default USER
        switch (perfil) {
            case USER: return 1;
            case ADMIN: return 2;
            default: return 1;
        }
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.id = usuario.getId();
        dto.nome = usuario.getNome();
        dto.email = usuario.getEmail();
        dto.status = usuario.getStatus();
        dto.perfil = usuario.getPerfil();
        dto.telefone = usuario.getTelefone();
        dto.empresaId = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null;

        return dto;
    }
}
