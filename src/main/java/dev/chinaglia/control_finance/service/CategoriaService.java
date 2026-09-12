package dev.chinaglia.control_finance.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import dev.chinaglia.control_finance.dto.request.CategoriaRequest;
import dev.chinaglia.control_finance.dto.response.CategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.CategoriaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.UsuarioNaoEncontradoException;
import dev.chinaglia.control_finance.mapstruct.CategoriaMapper;
import dev.chinaglia.control_finance.repository.CategoriaRepository;
import dev.chinaglia.control_finance.repository.UsuarioRepository;

@Service
public class CategoriaService {
	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;
	private final UsuarioRepository usuarioRepository;

	public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper,
			UsuarioRepository usuarioRepository) {
		this.categoriaRepository = categoriaRepository;
		this.categoriaMapper = categoriaMapper;
		this.usuarioRepository = usuarioRepository;
	}

	public CategoriaResponse save(CategoriaRequest categoriaRequest) {
		if (categoriaRequest == null) {
			throw new CategoriaNaoEncontradaException("Informe uma categoria válida");
		}
		Usuario usuario = getUsuarioAutenticado();
		Categoria categoria = categoriaMapper.toCategoriaEntity(categoriaRequest);
		categoria.setUsuario(usuario);
		categoriaRepository.save(categoria);
		return categoriaMapper.toCategoriaResponse(categoria);
	}

	public List<CategoriaResponse> findAll() {
		Usuario usuario = getUsuarioAutenticado();
		List<Categoria> categorias = categoriaRepository.findByStatusTrueAndUsuarioId(usuario.getId());
		List<CategoriaResponse> categoriaResponses = new ArrayList<>();
		for (Categoria categoria : categorias) {
			categoriaResponses.add(categoriaMapper.toCategoriaResponse(categoria));
		}
		return categoriaResponses;
	}

	public CategoriaResponse updateStatus(Long id) {
		if (id == null || id <= 0) {
			throw new CategoriaNaoEncontradaException("Informe uma categoria para remover a categoria");
		}
		Usuario usuario = getUsuarioAutenticado();
		Categoria categoria = categoriaRepository.findByIdAndStatusTrueAndUsuarioId(id, usuario.getId())
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe"));
		categoria.setStatus(false);
		categoriaRepository.save(categoria);
		return categoriaMapper.toCategoriaResponse(categoria);
	}

	public CategoriaResponse update(Long id, CategoriaRequest categoriaRequest) {
		if (id == null || id <= 0) {
			throw new CategoriaNaoEncontradaException("Informe uma categoria para atualizar a categoria");
		}
		if (categoriaRequest == null) {
			throw new CategoriaNaoEncontradaException("Informe uma categoria válida para realizar a atualização");
		}
		Usuario usuario = getUsuarioAutenticado();
		Categoria categoria = categoriaRepository.findByIdAndStatusTrueAndUsuarioId(id, usuario.getId())
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe"));
		categoria.setNome(categoriaRequest.nome());
		categoriaRepository.save(categoria);
		return categoriaMapper.toCategoriaResponse(categoria);
	}

	private Usuario getUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("Usuário não autenticado");
		}
		String email = authentication.getName();
		if (email == null || email.isBlank()) {
			throw new RuntimeException("Usuário autenticado não possui email");
		}
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
	}
}