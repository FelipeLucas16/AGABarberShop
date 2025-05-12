package com.devffl.babershop.entities;

import jakarta.persistence.*;
import lombok.*;
import com.devffl.babershop.entities.User;
import java.util.List;

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
    private List<Servicos> servicos;
    private List<Produto> produtos;
    private Double valorTotal;
}
