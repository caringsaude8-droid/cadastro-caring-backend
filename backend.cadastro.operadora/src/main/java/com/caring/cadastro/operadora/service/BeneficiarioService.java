package com.caring.cadastro.operadora.service;

import com.caring.cadastro.operadora.domain.entity.Beneficiario;
import com.caring.cadastro.operadora.domain.entity.Empresa;
import com.caring.cadastro.operadora.domain.repository.BeneficiarioRepository;
import com.caring.cadastro.operadora.domain.repository.EmpresaRepository;
import com.caring.cadastro.operadora.dto.BeneficiarioRequestDTO;
import com.caring.cadastro.operadora.dto.BeneficiarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiarioService {
    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    public BeneficiarioResponseDTO criarBeneficiario(BeneficiarioRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.benEmpId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setEmpresa(empresa);
        beneficiario.setBenTipoMotivo(dto.benTipoMotivo);
        beneficiario.setBenCodUnimedSeg(dto.benCodUnimedSeg);
        beneficiario.setBenRelacaoDep(dto.benRelacaoDep);
        beneficiario.setBenDtaNasc(dto.benDtaNasc);
        beneficiario.setBenSexo(dto.benSexo);
        beneficiario.setBenEstCivil(dto.benEstCivil);
        beneficiario.setBenDtaInclusao(dto.benDtaInclusao);
        beneficiario.setBenDtaExclusao(dto.benDtaExclusao);
        beneficiario.setBenPlanoProd(dto.benPlanoProd);
        beneficiario.setBenNomeSegurado(dto.benNomeSegurado);
        beneficiario.setBenCpf(dto.benCpf);
        beneficiario.setBenCidade(dto.benCidade);
        beneficiario.setBenUf(dto.benUf);
        beneficiario.setBenAdmissao(dto.benAdmissao);
        beneficiario.setBenNomeDaMae(dto.benNomeDaMae);
        beneficiario.setBenEndereco(dto.benEndereco);
        beneficiario.setBenComplemento(dto.benComplemento);
        beneficiario.setBenBairro(dto.benBairro);
        beneficiario.setBenCep(dto.benCep);
        beneficiario.setBenMatricula(dto.benMatricula);
        beneficiario.setBenDddCel(dto.benDddCel);
        beneficiario.setBenEmail(dto.benEmail);
        beneficiario.setBenDataCasamento(dto.benDataCasamento);
        beneficiario.setBenIndicPesTrans(dto.benIndicPesTrans);
        beneficiario.setBenNomeSocial(dto.benNomeSocial);
        beneficiario.setBenIdentGenero(dto.benIdentGenero);
        beneficiario.setBenCodCartao(dto.benCodCartao);
        beneficiario.setBenMotivoExclusao(dto.benMotivoExclusao);
        beneficiario.setBenStatus(dto.benStatus);
        beneficiario.setBenNumero(dto.benNumero);
        if (dto.benTitularId != null) {
            beneficiario.setTitular(beneficiarioRepository.findById(dto.benTitularId).orElse(null));
        }
        beneficiario = beneficiarioRepository.save(beneficiario);
        return toResponseDTO(beneficiario);
    }

    private BeneficiarioResponseDTO toResponseDTO(Beneficiario beneficiario) {
        BeneficiarioResponseDTO dto = new BeneficiarioResponseDTO();
        dto.id = beneficiario.getId();
        dto.benEmpId = beneficiario.getEmpresa() != null ? beneficiario.getEmpresa().getId() : null;
        dto.benTipoMotivo = beneficiario.getBenTipoMotivo();
        dto.benCodUnimedSeg = beneficiario.getBenCodUnimedSeg();
        dto.benRelacaoDep = beneficiario.getBenRelacaoDep();
        dto.benDtaNasc = beneficiario.getBenDtaNasc();
        dto.benSexo = beneficiario.getBenSexo();
        dto.benEstCivil = beneficiario.getBenEstCivil();
        dto.benDtaInclusao = beneficiario.getBenDtaInclusao();
        dto.benDtaExclusao = beneficiario.getBenDtaExclusao();
        dto.benPlanoProd = beneficiario.getBenPlanoProd();
        dto.benNomeSegurado = beneficiario.getBenNomeSegurado();
        dto.benCpf = beneficiario.getBenCpf();
        dto.benCidade = beneficiario.getBenCidade();
        dto.benUf = beneficiario.getBenUf();
        dto.benAdmissao = beneficiario.getBenAdmissao();
        dto.benNomeDaMae = beneficiario.getBenNomeDaMae();
        dto.benEndereco = beneficiario.getBenEndereco();
        dto.benComplemento = beneficiario.getBenComplemento();
        dto.benBairro = beneficiario.getBenBairro();
        dto.benCep = beneficiario.getBenCep();
        dto.benMatricula = beneficiario.getBenMatricula();
        dto.benDddCel = beneficiario.getBenDddCel();
        dto.benEmail = beneficiario.getBenEmail();
        dto.benDataCasamento = beneficiario.getBenDataCasamento();
        dto.benIndicPesTrans = beneficiario.getBenIndicPesTrans();
        dto.benNomeSocial = beneficiario.getBenNomeSocial();
        dto.benIdentGenero = beneficiario.getBenIdentGenero();
        dto.benTitularId = beneficiario.getTitular() != null ? beneficiario.getTitular().getId() : null;
        dto.benCodCartao = beneficiario.getBenCodCartao();
        dto.benMotivoExclusao = beneficiario.getBenMotivoExclusao();
        dto.benStatus = beneficiario.getBenStatus();
        dto.benNumero = beneficiario.getBenNumero();
        return dto;
    }

    public List<BeneficiarioResponseDTO> listarBeneficiarios() {
        return beneficiarioRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public BeneficiarioResponseDTO buscarPorId(Long id) {
        return beneficiarioRepository.findById(id).map(this::toResponseDTO).orElse(null);
    }

    public BeneficiarioResponseDTO buscarPorIdToDTO(Long id) {
        return beneficiarioRepository.findById(id).map(this::toResponseDTO).orElse(null);
    }

    public BeneficiarioResponseDTO atualizarBeneficiario(Long id, BeneficiarioRequestDTO dto) {
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado"));
        Empresa empresa = empresaRepository.findById(dto.benEmpId)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        beneficiario.setEmpresa(empresa);
        beneficiario.setBenTipoMotivo(dto.benTipoMotivo);
        beneficiario.setBenCodUnimedSeg(dto.benCodUnimedSeg);
        beneficiario.setBenRelacaoDep(dto.benRelacaoDep);
        beneficiario.setBenDtaNasc(dto.benDtaNasc);
        beneficiario.setBenSexo(dto.benSexo);
        beneficiario.setBenEstCivil(dto.benEstCivil);
        beneficiario.setBenDtaInclusao(dto.benDtaInclusao);
        beneficiario.setBenDtaExclusao(dto.benDtaExclusao);
        beneficiario.setBenPlanoProd(dto.benPlanoProd);
        beneficiario.setBenNomeSegurado(dto.benNomeSegurado);
        beneficiario.setBenCpf(dto.benCpf);
        beneficiario.setBenCidade(dto.benCidade);
        beneficiario.setBenUf(dto.benUf);
        beneficiario.setBenAdmissao(dto.benAdmissao);
        beneficiario.setBenNomeDaMae(dto.benNomeDaMae);
        beneficiario.setBenEndereco(dto.benEndereco);
        beneficiario.setBenComplemento(dto.benComplemento);
        beneficiario.setBenBairro(dto.benBairro);
        beneficiario.setBenCep(dto.benCep);
        beneficiario.setBenMatricula(dto.benMatricula);
        beneficiario.setBenDddCel(dto.benDddCel);
        beneficiario.setBenEmail(dto.benEmail);
        beneficiario.setBenDataCasamento(dto.benDataCasamento);
        beneficiario.setBenIndicPesTrans(dto.benIndicPesTrans);
        beneficiario.setBenNomeSocial(dto.benNomeSocial);
        beneficiario.setBenIdentGenero(dto.benIdentGenero);
        beneficiario.setBenCodCartao(dto.benCodCartao);
        beneficiario.setBenMotivoExclusao(dto.benMotivoExclusao);
        beneficiario.setBenStatus(dto.benStatus);
        beneficiario.setBenNumero(dto.benNumero);
        if (dto.benTitularId != null) {
            beneficiario.setTitular(beneficiarioRepository.findById(dto.benTitularId).orElse(null));
        }
        beneficiario = beneficiarioRepository.save(beneficiario);
        return toResponseDTO(beneficiario);
    }

    public void deletarBeneficiario(Long id) {
        beneficiarioRepository.deleteById(id);
    }
}
