package com.devffl.babershop.entities;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.enums.MetodoPagamento;
import com.devffl.babershop.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import com.devffl.babershop.entities.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_ordem_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;
    @ManyToMany
    @JoinTable(
            name = "ordem_servico_servicos",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servicos> servicos;
    @ManyToMany
    @JoinTable(
            name = "ordem_servico_produto",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private List<Produto> produtos;
    private Double valorTotal;
    @Enumerated(EnumType.STRING)
    private StatusPagamento status = StatusPagamento.ABERTO;
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    public OrdemServicoDto toDto() {
        return OrdemServicoDto.builder()
                .id(this.id)
                .user(this.user != null ? this.user.toDto() : null)
                .dataCriacao(this.dataCriacao)
                .servicos(this.servicos != null ?
                        this.servicos.stream().map(Servicos::toDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .produtos(this.produtos != null ?
                        this.produtos.stream().map(Produto::toDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .valorTotal(this.valorTotal)
                .status(this.status)
                .metodoPagamento(this.metodoPagamento)
                .build();
    }
}