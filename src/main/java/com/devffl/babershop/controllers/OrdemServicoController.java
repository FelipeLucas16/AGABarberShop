package com.devffl.babershop.controllers;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.services.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
