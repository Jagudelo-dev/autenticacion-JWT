package com.JoseMiguelAgudelo.autenticacion_JWT.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JoseMiguelAgudelo.autenticacion_JWT.dto.HttpGlobalResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.LoginResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.RegisterRequestDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.UserDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final AuthService authService;

	@PostMapping("refresh")
	public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refreshToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(createResponse("Token is invalid", null));
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length());
		try {
			return ResponseEntity.ok(authService.refreshToken(token));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(createResponse("Token is invalid or expired", null));
		}
	}

	@PostMapping("register")
	public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(
			@RequestBody RegisterRequestDTO registerRequest) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));
		} catch (Exception exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	private <T> HttpGlobalResponseDTO<T> createResponse(String message, T data) {
		HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
		response.setMessage(message);
		response.setData(data);
		return response;
	}
}
