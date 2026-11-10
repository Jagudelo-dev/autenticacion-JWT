package com.JoseMiguelAgudelo.autenticacion_JWT.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JoseMiguelAgudelo.autenticacion_JWT.dto.HttpGlobalResponseDTO;

@RestController
@RequestMapping("/user")
public class UserController {

	@GetMapping("profile")
	public HttpGlobalResponseDTO<Map<String, Object>> getProfile(
			@RequestAttribute("userId") Long userId,
			@RequestAttribute("username") String username) {
		HttpGlobalResponseDTO<Map<String, Object>> response = new HttpGlobalResponseDTO<>();
		response.setMessage("Perfil de " + username);
		response.setData(Map.of("userId", userId, "username", username));
		return response;
	}
}
