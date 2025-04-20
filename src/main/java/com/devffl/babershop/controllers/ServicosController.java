package com.devffl.babershop.controllers;

import com.devffl.babershop.services.ServicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/servicos")
public class ServicosController {

    @Autowired
    private ServicosService servicosService;
}
