package com.devffl.babershop.controllers;

import org.springframework.http.ResponseEntity;
import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.entities.Agendamento;
import com.devffl.babershop.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping (value = "/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping
    public List<AgendamentoDto> listarAgendamentos() {
        return agendamentoService.findAll();
    }

    @PostMapping
    public ResponseEntity<AgendamentoDto> agendar(@RequestBody AgendamentoDto dto) {
        AgendamentoDto response = agendamentoService.agendar(dto);
        return ResponseEntity.ok(response);
    }
}
