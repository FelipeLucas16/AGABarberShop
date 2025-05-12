package com.devffl.babershop.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String preco;
    private String descricao;

}
