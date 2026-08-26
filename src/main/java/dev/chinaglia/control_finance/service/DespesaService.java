package dev.chinaglia.control_finance.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;
import dev.chinaglia.control_finance.entitdades.Despesa;
import dev.chinaglia.control_finance.entitdades.Prioridade;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.CategoriaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.DespesaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.PrioridadeNaoEncontradaException;
import dev.chinaglia.control_finance.exception.UsuarioNaoEncontradoException;
import dev.chinaglia.control_finance.mapstruct.DespesaMapper;
import dev.chinaglia.control_finance.repository.CategoriaRepository;
import dev.chinaglia.control_finance.repository.DespesaRepository;
import dev.chinaglia.control_finance.repository.PrioridadeRepository;
import dev.chinaglia.control_finance.repository.UsuarioRepository;

@Service
public class DespesaService {

	private final DespesaRepository    despesaRepository;
	private final CategoriaRepository  categoriaRepository;
	private final PrioridadeRepository prioridadeRepository;
	private final DespesaMapper despesaMapper;
	private final UsuarioRepository usuarioRepository;
	
	public DespesaService(DespesaRepository despesaRepository,CategoriaRepository categoriaRepository,PrioridadeRepository prioridadeRepository,DespesaMapper despesaMapper,UsuarioRepository usuarioRepository) {
	    this.despesaRepository = despesaRepository;
	    this.categoriaRepository = categoriaRepository;
	    this.prioridadeRepository = prioridadeRepository;
	    this.despesaMapper = despesaMapper;
	    this.usuarioRepository = usuarioRepository;
	}
	
	public DespesaResponse save (DespesaRequest despesaRequest) 
	{
		if(despesaRequest == null) { throw new DespesaNaoEncontradaException("Informe uma despesa valida"); }
		
		Categoria categoria = categoriaRepository.findById(despesaRequest.categoria()).orElseThrow(() -> new CategoriaNaoEncontradaException( "Categoria informada não existe, tente informar uma válida"));
		
		Prioridade prioridade = prioridadeRepository.findById(despesaRequest.prioridade()).orElseThrow(() -> new PrioridadeNaoEncontradaException("Prioridade informada não existe, tente informar uma válida"));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) 
		{
		    throw new RuntimeException("Usuário não autenticado");
		}
		
		
		String email = authentication.getName();

		if (email == null || email.isBlank()) {
		    throw new RuntimeException("Usuário autenticado não possui email");
		}
		
		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
		
		Despesa despesa = despesaMapper.toDespesaEntity(despesaRequest);
		
		despesa.setCategoria(categoria);
		despesa.setPrioridade(prioridade);
		despesa.setUsuario(usuario);
		
		despesaRepository.save(despesa);
		
		return despesaMapper.toDespesaResponse(despesa);
	}
	
	public List<DespesaResponse> findAll()
	{
		List<Despesa> despesas = despesaRepository.findByStatusTrue();
		
		List<DespesaResponse> despesasResponses = new ArrayList<DespesaResponse>();
		
		for(int i = 0; i < despesas.size(); i++) 
		{
			Despesa despesa = despesas.get(i);
			despesasResponses.add(despesaMapper.toDespesaResponse(despesa));
 		}
		
		return despesasResponses;
	}
	
	public DespesaResponse updateStatus(Long id) {

	    if (id == null || id <= 0) {throw new DespesaNaoEncontradaException( "Informe uma despesa para remover a despesa");
	    }

	    Despesa despesa = despesaRepository
	        .findByIdAndStatusTrue(id)
	        .orElseThrow(() -> new DespesaNaoEncontradaException(
	            "Despesa informada não existe"
	        ));

	    despesa.setStatus(false);

	    despesaRepository.save(despesa);

	    return despesaMapper.toDespesaResponse(despesa);
	}
	
	public DespesaResponse update(Long id, DespesaRequest despesaRequest) {

	    if (id == null || id <= 0) {
	        throw new DespesaNaoEncontradaException(
	            "Informe uma despesa válida"
	        );
	    }

	    if (despesaRequest == null) {
	        throw new DespesaNaoEncontradaException(
	            "Informe os dados da despesa"
	        );
	    }

	    Despesa despesa = despesaRepository
	            .findByIdAndStatusTrue(id)
	            .orElseThrow(() -> new DespesaNaoEncontradaException(
	                "Despesa informada não existe"
	            ));

	    Categoria categoria = categoriaRepository
	            .findById(despesaRequest.categoria())
	            .orElseThrow(() -> new CategoriaNaoEncontradaException(
	                "Categoria informada não existe, tente informar uma válida"
	            ));

	    Prioridade prioridade = prioridadeRepository
	            .findById(despesaRequest.prioridade())
	            .orElseThrow(() -> new PrioridadeNaoEncontradaException(
	                "Prioridade informada não existe, tente informar uma válida"
	            ));

	    despesa.setNome(despesaRequest.nome());
	    despesa.setDataVencimento(despesaRequest.dataVencimento());
	    despesa.setValor(despesaRequest.valor());
	    despesa.setDescricao(despesaRequest.descricao());
	    despesa.setCategoria(categoria);
	    despesa.setPrioridade(prioridade);

	    despesaRepository.save(despesa);

	    return despesaMapper.toDespesaResponse(despesa);
	}
	
}
