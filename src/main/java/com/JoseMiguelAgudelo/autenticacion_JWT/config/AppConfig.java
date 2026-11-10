package com.JoseMiguelAgudelo.autenticacion_JWT.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Roles;
import com.JoseMiguelAgudelo.autenticacion_JWT.repository.RolRepository;

@Configuration
public class AppConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner initializeRoles(RolRepository rolRepository) {
		return args -> {
			if (rolRepository.count() == 0) {
				Roles adminRole = new Roles();
				adminRole.setName("ADMIN");
				adminRole.setDescription("Administrador");

				Roles userRole = new Roles();
				userRole.setName("USER");
				userRole.setDescription("Usuario");

				rolRepository.save(adminRole);
				rolRepository.save(userRole);
			}
		};
	}
}
