package com.devffl.babershop.repositories;

import com.devffl.babershop.entities.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
}
