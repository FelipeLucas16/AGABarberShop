package com.devffl.babershop.services;

import com.devffl.babershop.repositories.ServicosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicosService {

    @Autowired
    private ServicosRepository servicosRepository;

}
