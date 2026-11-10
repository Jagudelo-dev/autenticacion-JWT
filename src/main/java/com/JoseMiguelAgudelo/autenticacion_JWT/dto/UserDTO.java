package com.JoseMiguelAgudelo.autenticacion_JWT.dto;

import lombok.Data;

@Data
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private Long rolId;
}
