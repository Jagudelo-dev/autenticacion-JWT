package com.JoseMiguelAgudelo.autenticacion_JWT.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.JoseMiguelAgudelo.autenticacion_JWT.dto.HttpGlobalResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.LoginRequestDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.LoginResponseDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.RegisterRequestDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.dto.UserDTO;
import com.JoseMiguelAgudelo.autenticacion_JWT.entity.Users;
import com.JoseMiguelAgudelo.autenticacion_JWT.repository.RolRepository;
import com.JoseMiguelAgudelo.autenticacion_JWT.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RolRepository rolRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void testLoginSuccess() {
		LoginRequestDTO request = createLoginRequest();
		Users user = new Users();
		user.setId(42L);
		user.setUsername(request.getUsername());
		user.setPassword("encoded-password");
		user.setRolId(2L);
		when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtService.generateToken(user.getId(), user.getUsername(), user.getRolId())).thenReturn("jwt-token");

		HttpGlobalResponseDTO<LoginResponseDTO> response = authService.login(request);

		assertEquals("Login exitoso", response.getMessage());
		assertEquals("jwt-token", response.getData().getJwt());
	}

	@Test
	void testLoginInvalidCredentials() {
		LoginRequestDTO request = createLoginRequest();
		Users user = new Users();
		user.setPassword("encoded-password");
		when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

		HttpGlobalResponseDTO<LoginResponseDTO> response = authService.login(request);

		assertEquals("Credenciales inválidas", response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testBCryptHashesAreDifferentButMatch() {
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		String password = "test-password";

		String firstHash = bcrypt.encode(password);
		String secondHash = bcrypt.encode(password);

		assertNotEquals(firstHash, secondHash);
		assertTrue(bcrypt.matches(password, firstHash));
		assertTrue(bcrypt.matches(password, secondHash));
	}

	@Test
	void testRegisterUserAlreadyExists() {
		RegisterRequestDTO request = createRegisterRequest();
		Users existingUser = new Users();
		when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

		HttpGlobalResponseDTO<UserDTO> response = authService.register(request);

		assertEquals("Nombre de usuario en uso", response.getMessage());
	}

	@Test
	void testRegisterSuccess() {
		RegisterRequestDTO request = createRegisterRequest();
		Users savedUser = new Users();
		savedUser.setId(42L);
		savedUser.setUsername(request.getUsername());
		savedUser.setEmail(request.getEmail());
		savedUser.setPassword("encoded-password");
		savedUser.setRolId(request.getRolId());

		when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
		when(rolRepository.existsById(request.getRolId())).thenReturn(true);
		when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
		when(userRepository.save(any(Users.class))).thenReturn(savedUser);

		HttpGlobalResponseDTO<UserDTO> response = authService.register(request);

		verify(userRepository).save(any(Users.class));
		assertEquals("usuario registrado correctamente", response.getMessage());
		assertEquals(42L, response.getData().getId());
	}

	private RegisterRequestDTO createRegisterRequest() {
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setUsername("jane");
		request.setPassword("test-password");
		request.setEmail("jane@example.com");
		request.setRolId(2L);
		return request;
	}

	private LoginRequestDTO createLoginRequest() {
		LoginRequestDTO request = new LoginRequestDTO();
		request.setUsername("jane");
		request.setPassword("test-password");
		return request;
	}
}
