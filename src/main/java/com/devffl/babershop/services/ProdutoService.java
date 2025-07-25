package com.devffl.babershop.services;

import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.entities.OrdemServico;
import com.devffl.babershop.entities.Produto;
import com.devffl.babershop.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    public ProdutoRepository produtoRepository;

    @Transactional
    public List<ProdutoDto> findAll() {
        return produtoRepository.findAll().stream()
                .map(Produto::toDto)
                .toList();
    }

    @Transactional
    public ProdutoDto novoProduto(ProdutoDto produtoDto) {
        Produto produto = new Produto();
        produto.setId(produtoDto.getId());
        produto.setNome(produtoDto.getNome());
        produto.setPreco(produtoDto.getPreco());
        produto.setDescricao(produtoDto.getDescricao());

        Produto novoProduto = produtoRepository.save(produto);

        return novoProduto.toDto();
    }

    @Transactional
    public void deleteById(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado."));
        produtoRepository.delete(produto);
    }

}
