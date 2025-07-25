package com.devffl.babershop.controllers;

import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.services.ProdutoService;
import com.devffl.babershop.services.ServicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<ProdutoDto> findAll() {
        return produtoService.findAll();
    }

    @PostMapping
    public ResponseEntity<ProdutoDto> novoProduto(@RequestBody ProdutoDto dto) {
        ProdutoDto response = produtoService.novoProduto(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        produtoService.deleteById(id);
        return  ResponseEntity.noContent().build();
    }
}
