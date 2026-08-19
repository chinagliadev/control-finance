package dev.chinaglia.control_finance.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.config.TokenConfig;
import dev.chinaglia.control_finance.dto.request.LoginRequest;
import dev.chinaglia.control_finance.dto.request.RegistrarUsuarioRequest;
import dev.chinaglia.control_finance.dto.response.LoginResponse;
import dev.chinaglia.control_finance.dto.response.RegistrarUsuarioResponse;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final TokenConfig tokenConfig;
	private final AuthService authService;
	
	public AuthController(AuthenticationManager authenticationManager, TokenConfig tokenConfig ,AuthService authService) {
		this.authenticationManager = authenticationManager;
		this.tokenConfig = tokenConfig;
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
	{
		return ResponseEntity.ok(authService.login(loginRequest));
	}
	
	@PostMapping("/registrar")
	public ResponseEntity<RegistrarUsuarioResponse> login(@Valid @RequestBody  RegistrarUsuarioRequest registrarUsuarioRequest)
	{
		RegistrarUsuarioResponse registrarUsuarioResponse = authService.registrar(registrarUsuarioRequest);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(registrarUsuarioResponse.id()).toUri();
		
		return ResponseEntity.created(location).body(registrarUsuarioResponse);
	}
	
}
