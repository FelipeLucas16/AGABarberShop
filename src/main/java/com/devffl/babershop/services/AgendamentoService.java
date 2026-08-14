package com.devffl.babershop.services;

import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.entities.Agendamento;
import com.devffl.babershop.entities.User;
import com.devffl.babershop.enums.RoleName;
import com.devffl.babershop.repositories.AgendamentoRepository;
import com.devffl.babershop.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public List<AgendamentoDto> findAll() {
        User usuarioAutenticado = userService.getAuthenticatedUser();

        List<Agendamento> agendamentos = isAdmin(usuarioAutenticado)
                ? agendamentoRepository.findAll()
                : agendamentoRepository.findByUserId(usuarioAutenticado.getId());

        return agendamentos.stream()
                .map(Agendamento::toDto)
                .toList();
    }

    @Transactional
    public AgendamentoDto agendar(AgendamentoDto agendamentoDto) {
        if (agendamentoDto.getDataHora() == null) {
            throw new IllegalArgumentException("Dados inválidos");
        }

        User usuarioAutenticado = userService.getAuthenticatedUser();
        User dono = resolverDono(usuarioAutenticado, agendamentoDto.getUserId());

        Agendamento agendamento = new Agendamento();
        agendamento.setUser(dono);
        agendamento.setDataHora(agendamentoDto.getDataHora());
        agendamento.setStatus("AGENDADO");

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return agendamentoSalvo.toDto();
    }

    @Transactional
    public void deleteById(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));
        checarPropriedade(agendamento);
        agendamentoRepository.delete(agendamento);
    }

    @Transactional
    public AgendamentoDto atualizarAgendamento (Long id, AgendamentoDto agendamentoDto) {

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        User usuarioAutenticado = checarPropriedade(agendamento);
        User dono = resolverDono(usuarioAutenticado, agendamentoDto.getUserId());

        agendamento.setUser(dono);
        agendamento.setDataHora(agendamentoDto.getDataHora());
        agendamento.setStatus(agendamentoDto.getStatus());

        agendamentoRepository.save(agendamento);

        return agendamento.toDto();
    }

    /**
     * Usuário comum sempre vira dono do próprio agendamento, mesmo que informe outro
     * userId no corpo da requisição. Admin pode agendar em nome de outro usuário.
     */
    private User resolverDono(User usuarioAutenticado, Long userIdInformado) {
        if (isAdmin(usuarioAutenticado) && userIdInformado != null) {
            return userRepository.findById(userIdInformado)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        }
        return usuarioAutenticado;
    }

    private User checarPropriedade(Agendamento agendamento) {
        User usuarioAutenticado = userService.getAuthenticatedUser();
        if (!isAdmin(usuarioAutenticado) && !agendamento.getUser().getId().equals(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar este agendamento");
        }
        return usuarioAutenticado;
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.ROLE_ADMINISTRADOR);
    }
}
