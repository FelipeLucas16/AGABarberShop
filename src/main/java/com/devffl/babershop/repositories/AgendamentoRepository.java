package com.devffl.babershop.repositories;

import com.devffl.babershop.entities.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    Optional<Agendamento> findByDataHora(LocalDateTime dataHora);
    boolean existsByUserIdAndDataHora(Long userId, LocalDateTime dataHora);
    boolean existsByDataHoraAndUserIdNot(LocalDateTime dataHora, Long userId);
}
