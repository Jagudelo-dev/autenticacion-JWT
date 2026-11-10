package com.JoseMiguelAgudelo.autenticacion_JWT.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Roles;

public interface RolRepository extends JpaRepository<Roles, Long> {
}
