package com.devffl.babershop.repositories;

import com.devffl.babershop.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository  extends JpaRepository<Produto, Long> {
}
