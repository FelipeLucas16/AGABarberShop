package com.devffl.babershop.repositories;

import com.devffl.babershop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
