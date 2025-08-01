package com.devffl.babershop.services;

import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.entities.OrdemServico;
import com.devffl.babershop.entities.Produto;
import com.devffl.babershop.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public ProdutoDto atualizarproduto (Long id, ProdutoDto produtoDto) {
        Produto produto = produtoRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Produto não encontrado."));

        produto.setNome(produtoDto.getNome());
        produto.setPreco(produtoDto.getPreco());
        produto.setDescricao(produtoDto.getDescricao());

        produtoRepository.save(produto);
        return produto.toDto();
    }

}
