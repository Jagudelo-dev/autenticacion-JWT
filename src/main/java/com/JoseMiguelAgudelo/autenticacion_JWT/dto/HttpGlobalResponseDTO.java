package com.JoseMiguelAgudelo.autenticacion_JWT.dto;

import lombok.Data;

/** Estandariza las respuestas HTTP de la API. */
@Data
public class HttpGlobalResponseDTO<T> {

	private String message;
	private T data;
}
