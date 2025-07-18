package com.devffl.babershop.entities;

import java.util.Objects;

import com.devffl.babershop.dto.AgendamentoDto;
import com.devffl.babershop.dto.ServicoDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tb_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicos {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Double preco;
	private String descricao;

	public ServicoDto toDto() {
		return ServicoDto.builder()
				.id(this.id)
				.nome(this.nome)
				.preco(this.preco)
				.descricao(this.descricao)
				.build();
	}
}
