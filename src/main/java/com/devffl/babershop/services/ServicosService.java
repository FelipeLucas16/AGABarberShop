package com.devffl.babershop.services;

import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.entities.Produto;
import com.devffl.babershop.entities.Servicos;
import com.devffl.babershop.repositories.ServicosRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicosService {

    @Autowired
    private ServicosRepository servicosRepository;

    @Transactional
    public List<ServicoDto> findAll() {
        return servicosRepository.findAll().stream()
                .map(Servicos::toDto)
                .toList();
    }

    @Transactional
    public ServicoDto nevoServico(ServicoDto servicoDto) {

        Servicos servico = new Servicos();
        servico.setId(servicoDto.getId());
        servico.setNome(servicoDto.getNome());
        servico.setPreco(servicoDto.getPreco());
        servico.setDescricao(servicoDto.getDescricao());

        Servicos servicoSalvo = servicosRepository.save(servico);

        return servicoSalvo.toDto();

    }

    @Transactional
    public void deleteById(Long id) {
        Servicos servicos = servicosRepository.findById(id).orElseThrow(() -> new RuntimeException("Serviço não encontrado."));
        servicosRepository.delete(servicos);
    }

}
