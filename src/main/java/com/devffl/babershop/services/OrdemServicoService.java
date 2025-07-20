package com.devffl.babershop.services;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.entities.OrdemServico;
import com.devffl.babershop.entities.Produto;
import com.devffl.babershop.entities.Servicos;
import com.devffl.babershop.entities.User;
import com.devffl.babershop.repositories.OrdemServicoRepository;
import com.devffl.babershop.repositories.ProdutoRepository;
import com.devffl.babershop.repositories.ServicosRepository;
import com.devffl.babershop.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ServicosRepository servicosRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<OrdemServicoDto> findAll() {
        return ordemServicoRepository.findAll().stream()
                .map(OrdemServico::toDto)
                .toList();
    }

    @Transactional
    public OrdemServicoDto novaOrdemServico(OrdemServicoDto ordemServicoDto) {
        User user = userRepository.findById(ordemServicoDto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + ordemServicoDto.getUser().getId()));

        List<Long> servicoIds = ordemServicoDto.getServicos().stream()
                .map(ServicoDto::getId)
                .collect(Collectors.toList());
        List<Servicos> servicos = servicosRepository.findAllById(servicoIds);

        List<Long> produtoIds = ordemServicoDto.getProdutos().stream()
                .map(ProdutoDto::getId)
                .collect(Collectors.toList());
        List<Produto> produtos = produtoRepository.findAllById(produtoIds);

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setUser(user);
        ordemServico.setServicos(servicos);
        ordemServico.setProdutos(produtos);
        ordemServico.setValorTotal(calcularValorTotal(servicos, produtos));

        OrdemServico ordemSalva = ordemServicoRepository.save(ordemServico);
        return ordemSalva.toDto();
    }

    private Double calcularValorTotal(List<Servicos> servicos, List<Produto> produtos) {
        double totalServicos = servicos.stream().mapToDouble(Servicos::getPreco).sum();
        double totalProdutos = produtos.stream().mapToDouble(Produto::getPreco).sum();
        return totalServicos + totalProdutos;
    }
}
