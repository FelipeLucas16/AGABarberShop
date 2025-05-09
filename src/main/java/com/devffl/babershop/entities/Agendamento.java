package com.devffl.babershop.entities;

import com.devffl.babershop.dto.AgendamentoDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private LocalDateTime dataHora;

	@Column(nullable = false)
	private String status;  // Ex: "AGENDADO", "CANCELADO", "FINALIZADO"


	public AgendamentoDto toDto() {
		return AgendamentoDto.builder()
				.id(this.id)
				.userId(this.user.getId())
				.userEmail(this.user.getEmail())  // Opcional
				.dataHora(this.dataHora)
				.status(this.status)
				.build();
	}

	public static Agendamento fromDto(AgendamentoDto dto, User user) {
		return Agendamento.builder()
				.user(user)
				.dataHora(dto.getDataHora())
				.status(dto.getStatus())
				.build();
	}
}