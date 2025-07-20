package com.devffl.babershop.services;

import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.entities.Agendamento;
import com.devffl.babershop.entities.User;
import com.devffl.babershop.repositories.AgendamentoRepository;
import com.devffl.babershop.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<AgendamentoDto> findAll() {
        return agendamentoRepository.findAll().stream()
                .map(Agendamento::toDto)
                .toList();
    }

    @Transactional
    public AgendamentoDto agendar(AgendamentoDto agendamentoDto) {
        if (agendamentoDto.getUserId() == null || agendamentoDto.getDataHora() == null) {
            throw new IllegalArgumentException("Dados inválidos");
        }

        User user = userRepository.findById(agendamentoDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));


        Agendamento agendamento = new Agendamento();
        agendamento.setUser(user);
        agendamento.setDataHora(agendamentoDto.getDataHora());
        agendamento.setStatus("AGENDADO");

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return agendamentoSalvo.toDto();
    }

    @Transactional
    public void deleteById(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id).orElseThrow();
        agendamentoRepository.delete(agendamento);
    }
}
