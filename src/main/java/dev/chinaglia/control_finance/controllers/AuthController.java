package dev.chinaglia.control_finance.controllers;

import java.net.URI;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.config.TokenConfig;
import dev.chinaglia.control_finance.dto.request.LoginRequest;
import dev.chinaglia.control_finance.dto.request.RegistrarUsuarioRequest;
import dev.chinaglia.control_finance.dto.response.RegistrarUsuarioResponse;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.repository.UsuarioRepository;
import dev.chinaglia.control_finance.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final TokenConfig tokenConfig;
	private final AuthService authService;
	private final UsuarioRepository usuarioRepository;
	
	public AuthController(AuthenticationManager authenticationManager, TokenConfig tokenConfig ,AuthService authService, UsuarioRepository usuarioRepository) {
		this.authenticationManager = authenticationManager;
		this.tokenConfig = tokenConfig;
		this.authService = authService;
		this.usuarioRepository = usuarioRepository;
	}
	
	 @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
	 
	        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
	        authenticationManager.authenticate(authToken);
	 
	        Usuario usuario = usuarioRepository.findByEmail(request.email())
	                .orElseThrow();
	 
	        String token = tokenConfig.generateToken(usuario);
	 
	        ResponseCookie cookie = ResponseCookie.from("token", token)
	                .httpOnly(true)
	                .secure(true)              
	                .sameSite("None")
	                .path("/")
	                .maxAge(Duration.ofHours(24))
	                .build();
	 
	        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	 
	        return ResponseEntity.ok().build();
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
