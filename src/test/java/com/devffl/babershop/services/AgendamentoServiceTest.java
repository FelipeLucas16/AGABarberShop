package com.devffl.babershop.services;

import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.entities.Agendamento;
import com.devffl.babershop.entities.Role;
import com.devffl.babershop.entities.User;
import com.devffl.babershop.enums.RoleName;
import com.devffl.babershop.repositories.AgendamentoRepository;
import com.devffl.babershop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private User comum;
    private User outroComum;
    private User admin;

    @BeforeEach
    void setUp() {
        comum = User.builder().id(1L).email("comum@teste.com")
                .roles(List.of(Role.builder().id(1L).name(RoleName.ROLE_COMUM).build()))
                .build();

        outroComum = User.builder().id(2L).email("outro@teste.com")
                .roles(List.of(Role.builder().id(1L).name(RoleName.ROLE_COMUM).build()))
                .build();

        admin = User.builder().id(3L).email("admin@teste.com")
                .roles(List.of(Role.builder().id(2L).name(RoleName.ROLE_ADMINISTRADOR).build()))
                .build();
    }

    @Test
    void comumSempreViraDonoDoProprioAgendamentoAoAgendar() {
        when(userService.getAuthenticatedUser()).thenReturn(comum);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        AgendamentoDto dto = AgendamentoDto.builder()
                .userId(outroComum.getId()) // tenta forjar outro dono
                .dataHora(LocalDateTime.now())
                .build();

        AgendamentoDto salvo = agendamentoService.agendar(dto);

        assertThat(salvo.getUserId()).isEqualTo(comum.getId());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void adminPodeAgendarEmNomeDeOutroUsuario() {
        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(outroComum.getId())).thenReturn(Optional.of(outroComum));
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        AgendamentoDto dto = AgendamentoDto.builder()
                .userId(outroComum.getId())
                .dataHora(LocalDateTime.now())
                .build();

        AgendamentoDto salvo = agendamentoService.agendar(dto);

        assertThat(salvo.getUserId()).isEqualTo(outroComum.getId());
    }

    @Test
    void comumNaoConsegueEditarAgendamentoDeOutroUsuario() {
        Agendamento agendamentoDeOutro = Agendamento.builder()
                .id(10L).user(outroComum).dataHora(LocalDateTime.now()).status("AGENDADO").build();

        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamentoDeOutro));
        when(userService.getAuthenticatedUser()).thenReturn(comum);

        AgendamentoDto dto = AgendamentoDto.builder().dataHora(LocalDateTime.now()).status("CONCLUIDO").build();

        assertThatThrownBy(() -> agendamentoService.atualizarAgendamento(10L, dto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void comumNaoConsegueExcluirAgendamentoDeOutroUsuario() {
        Agendamento agendamentoDeOutro = Agendamento.builder()
                .id(10L).user(outroComum).dataHora(LocalDateTime.now()).status("AGENDADO").build();

        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamentoDeOutro));
        when(userService.getAuthenticatedUser()).thenReturn(comum);

        assertThatThrownBy(() -> agendamentoService.deleteById(10L))
                .isInstanceOf(AccessDeniedException.class);

        verify(agendamentoRepository, never()).delete(any());
    }

    @Test
    void adminConsegueEditarAgendamentoDeQualquerUsuario() {
        Agendamento agendamentoDeOutro = Agendamento.builder()
                .id(10L).user(outroComum).dataHora(LocalDateTime.now()).status("AGENDADO").build();

        when(agendamentoRepository.findById(10L)).thenReturn(Optional.of(agendamentoDeOutro));
        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        AgendamentoDto dto = AgendamentoDto.builder().dataHora(LocalDateTime.now()).status("CONCLUIDO").build();

        AgendamentoDto atualizado = agendamentoService.atualizarAgendamento(10L, dto);

        assertThat(atualizado.getStatus()).isEqualTo("CONCLUIDO");
    }

    @Test
    void comumVeSomenteOsProprioAgendamentosAoListar() {
        when(userService.getAuthenticatedUser()).thenReturn(comum);
        when(agendamentoRepository.findByUserId(comum.getId())).thenReturn(List.of(
                Agendamento.builder().id(1L).user(comum).dataHora(LocalDateTime.now()).status("AGENDADO").build()
        ));

        List<AgendamentoDto> resultado = agendamentoService.findAll();

        assertThat(resultado).hasSize(1);
        verify(agendamentoRepository, never()).findAll();
    }
}
