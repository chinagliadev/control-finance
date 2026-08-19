package dev.chinaglia.control_finance.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.repository.UsuarioRepository;

@Service
public class AuthConfig implements UserDetailsService{

	private final UsuarioRepository usuarioRepository;
	
	public AuthConfig(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepository.findUserByEmail(username).orElseThrow(()->new UsernameNotFoundException(username));
	}

}
