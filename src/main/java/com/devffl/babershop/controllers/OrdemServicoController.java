package com.devffl.babershop.controllers;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.services.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
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

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.findById(id));
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
    public ResponseEntity<byte[]> gerarRelatorioOrdens(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ordemServicoService.gerarRelatorioPdf(inicio, fim);
    }

    @GetMapping("/relatorio/financeiro")
    public ResponseEntity<byte[]> gerarRelatorioFinanceiro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ordemServicoService.gerarRelatorioFinanceiroPdf(inicio, fim);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoDto> updateOrdemServico(@PathVariable Long id, @RequestBody OrdemServicoDto dto) {
        return ResponseEntity.ok(ordemServicoService.updateOrdemServico(id, dto));
    }
}
