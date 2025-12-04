package com.caring.cadastro.operadora.controller;

import com.caring.cadastro.operadora.dto.BeneficiarioRequestDTO;
import com.caring.cadastro.operadora.dto.BeneficiarioResponseDTO;
import com.caring.cadastro.operadora.service.BeneficiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cadastro/v1/beneficiarios")
public class BeneficiarioController {
    @Autowired
    private BeneficiarioService beneficiarioService;

    @PostMapping
    public ResponseEntity<BeneficiarioResponseDTO> criar(@RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO response = beneficiarioService.criarBeneficiario(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioResponseDTO>> listar() {
        return ResponseEntity.ok(beneficiarioService.listarBeneficiarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(@PathVariable Long id) {
        BeneficiarioResponseDTO response = beneficiarioService.buscarPorId(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO response = beneficiarioService.atualizarBeneficiario(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        beneficiarioService.deletarBeneficiario(id);
        return ResponseEntity.noContent().build();
    }
}
