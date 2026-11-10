package com.JoseMiguelAgudelo.autenticacion_JWT.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByUsername(String username);

	Optional<Users> findByEmail(String email);
}
