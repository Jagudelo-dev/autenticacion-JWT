package com.JoseMiguelAgudelo.autenticacion_JWT.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JoseMiguelAgudelo.autenticacion_JWT.dto.HttpGlobalResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.LoginResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.RegisterRequestDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.UserDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Users;
import com.JoseMiguelAgudelo.autenticacion_JWT.repository.RolRepository;
import com.JoseMiguelAgudelo.autenticacion_JWT.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final RolRepository rolRepository;
	private final PasswordEncoder passwordEncoder;

	public HttpGlobalResponseDTO<LoginResponseDTO> refreshToken(String token) throws Exception {
		String jwt = jwtService.refreshToken(token);
		LoginResponseDTO loginResponse = new LoginResponseDTO();
		loginResponse.setJwt(jwt);
		return createResponse("Token refrescado correctamente", loginResponse);
	}

	public HttpGlobalResponseDTO<UserDTO> register(RegisterRequestDTO request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return createResponse("Nombre de usuario en uso", null);
		}
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			return createResponse("correo en uso", null);
		}
		if (!rolRepository.existsById(request.getRolId())) {
			return createResponse("Rol no existe", null);
		}

		Users user = new Users();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setRolId(request.getRolId());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		Users savedUser = userRepository.save(user);
		UserDTO userDTO = new UserDTO();
		userDTO.setId(savedUser.getId());
		userDTO.setUsername(savedUser.getUsername());
		userDTO.setEmail(savedUser.getEmail());
		userDTO.setRolId(savedUser.getRolId());

		return createResponse("usuario registrado correctamente", userDTO);
	}

	private <T> HttpGlobalResponseDTO<T> createResponse(String message, T data) {
		HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
		response.setMessage(message);
		response.setData(data);
		return response;
	}
}
