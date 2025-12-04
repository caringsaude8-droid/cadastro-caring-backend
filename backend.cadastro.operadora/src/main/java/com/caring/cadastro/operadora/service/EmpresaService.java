package com.caring.cadastro.operadora.service;

import com.caring.cadastro.operadora.domain.entity.Empresa;
import com.caring.cadastro.operadora.domain.repository.EmpresaRepository;
import com.caring.cadastro.operadora.dto.EmpresaRequestDTO;
import com.caring.cadastro.operadora.dto.EmpresaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;

    public EmpresaResponseDTO criarEmpresa(EmpresaRequestDTO dto) {
        Empresa empresa = new Empresa();
        empresa.setNome(dto.getNome());
        empresa.setCnpj(dto.getCnpj());
        empresa.setCidade(dto.getCidade());
        empresa.setUf(dto.getUf());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setCodigoEmpresa(dto.getCodigoEmpresa());
        empresa.setNumeroEmpresa(dto.getNumeroEmpresa());
        empresa = empresaRepository.save(empresa);
        return toResponseDTO(empresa);
    }

    public List<EmpresaResponseDTO> listarEmpresas() {
        return empresaRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public Optional<EmpresaResponseDTO> buscarPorId(Long id) {
        return empresaRepository.findById(id).map(this::toResponseDTO);
    }

    public EmpresaResponseDTO atualizarEmpresa(Long id, EmpresaRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(id).orElseThrow();
        empresa.setNome(dto.getNome());
        empresa.setCnpj(dto.getCnpj());
        empresa.setCidade(dto.getCidade());
        empresa.setUf(dto.getUf());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setCodigoEmpresa(dto.getCodigoEmpresa());
        empresa.setNumeroEmpresa(dto.getNumeroEmpresa());
        empresa = empresaRepository.save(empresa);
        return toResponseDTO(empresa);
    }

    public void deletarEmpresa(Long id) {
        empresaRepository.deleteById(id);
    }

    private EmpresaResponseDTO toResponseDTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(empresa.getId());
        dto.setNome(empresa.getNome());
        dto.setCnpj(empresa.getCnpj());
        dto.setCidade(empresa.getCidade());
        dto.setUf(empresa.getUf());
        dto.setEmail(empresa.getEmail());
        dto.setTelefone(empresa.getTelefone());
        dto.setCodigoEmpresa(empresa.getCodigoEmpresa());
        dto.setNumeroEmpresa(empresa.getNumeroEmpresa());
        return dto;
    }
}
