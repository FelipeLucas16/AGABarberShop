package com.devffl.babershop.controllers;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.services.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/ordemservico")
public class OrdemServicoController {

    @Autowired
    OrdemServicoService ordemServicoService;

    @GetMapping
    public List<OrdemServicoDto> findAll() {
        return ordemServicoService.findAll();
    }

    @PostMapping
    public ResponseEntity<OrdemServicoDto> novaOrdemServico(@RequestBody OrdemServicoDto dto) {
        OrdemServicoDto response = ordemServicoService.novaOrdemServico(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        ordemServicoService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio/pdf")
    public ResponseEntity<byte[]> gerarRelatorioOrdens() {
        return ordemServicoService.gerarRelatorioPdf();
    }
}
