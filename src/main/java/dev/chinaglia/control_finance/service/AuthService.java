package dev.chinaglia.control_finance.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.config.TokenConfig;
import dev.chinaglia.control_finance.dto.request.LoginRequest;
import dev.chinaglia.control_finance.dto.request.RegistrarUsuarioRequest;
import dev.chinaglia.control_finance.dto.response.LoginResponse;
import dev.chinaglia.control_finance.dto.response.RegistrarUsuarioResponse;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.UsuarioJaCadastradoException;
import dev.chinaglia.control_finance.mapstruct.AuthMapper;
import dev.chinaglia.control_finance.repository.UsuarioRepository;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final TokenConfig tokenConfig;
	private final AuthMapper authMapper;
	
	public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig, AuthMapper authMapper) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.tokenConfig = tokenConfig;
		this.authMapper = authMapper;
	}

	public RegistrarUsuarioResponse registrar(RegistrarUsuarioRequest registrarUsuarioRequest) 
	{
		
		if(usuarioRepository.existsByEmail(registrarUsuarioRequest.email())) 
		{
			throw new UsuarioJaCadastradoException("Email informado já possui cadastro no sistema");
		}
		
		if(usuarioRepository.existsByCpf(registrarUsuarioRequest.cpf())) 
		{
			throw new UsuarioJaCadastradoException("CPF informado já possui cadastro no sistema");
		}
		
		Usuario usuario = authMapper.toUsuarioEntity(registrarUsuarioRequest);
		usuario.setSenha(passwordEncoder.encode(registrarUsuarioRequest.senha()));
		usuarioRepository.save(usuario);
			
		return authMapper.toRegistrarUsuarioResponse(usuario);
	}
	
	public LoginResponse login(LoginRequest loginRequest) {

	    UsernamePasswordAuthenticationToken usuarioSenha = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());

	    Authentication authentication = authenticationManager.authenticate(usuarioSenha);
	    Usuario usuario = (Usuario) authentication.getPrincipal();
	    String token = tokenConfig.generateToken(usuario);

	    return new LoginResponse(token);
	}
	
	
	
}
