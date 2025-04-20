package com.devffl.babershop.controllers;

import com.devffl.babershop.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;
}
