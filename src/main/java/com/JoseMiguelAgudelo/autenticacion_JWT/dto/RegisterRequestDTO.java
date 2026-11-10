package com.JoseMiguelAgudelo.autenticacion_JWT.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {

	private String username;
	private String password;
	private String email;
	private Long rolId;
}
