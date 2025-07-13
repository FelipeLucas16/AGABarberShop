package com.devffl.babershop.entities;

import com.devffl.babershop.dto.ProdutoDto;
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
    private Double preco;
    private String descricao;

    public ProdutoDto toDto() {
        return ProdutoDto.builder()
                .id(this.id)
                .nome(this.nome)
                .preco(this.preco)
                .descricao(this.descricao)
                .build();
    }

}
