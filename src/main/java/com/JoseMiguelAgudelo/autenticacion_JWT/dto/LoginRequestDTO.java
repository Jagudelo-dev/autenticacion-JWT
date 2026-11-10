package com.JoseMiguelAgudelo.autenticacion_JWT.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

	private String username;
	private String password;
}
