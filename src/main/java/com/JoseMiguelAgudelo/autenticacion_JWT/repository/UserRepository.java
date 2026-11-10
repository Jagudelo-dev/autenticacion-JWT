package com.JoseMiguelAgudelo.autenticacion_JWT.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
}
