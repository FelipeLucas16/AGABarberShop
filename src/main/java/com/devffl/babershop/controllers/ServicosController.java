package com.devffl.babershop.controllers;

import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.services.ServicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/servicos")
public class ServicosController {

    @Autowired
    private ServicosService servicosService;

    @GetMapping
    public List<ServicoDto> findAll() {
        return servicosService.findAll();
    }

    @PostMapping
    public ResponseEntity<ServicoDto> novoServico(@RequestBody ServicoDto dto) {
        ServicoDto response = servicosService.nevoServico(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        servicosService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }
}
