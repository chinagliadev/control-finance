package dev.chinaglia.control_finance.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.dto.request.CategoriaRequest;
import dev.chinaglia.control_finance.dto.response.CategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;
import dev.chinaglia.control_finance.exception.CategoriaNaoEncontradaException;
import dev.chinaglia.control_finance.mapstruct.CategoriaMapper;
import dev.chinaglia.control_finance.repository.CategoriaRepository;

@Service
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;
	
	public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) 
	{
		this.categoriaRepository = categoriaRepository;
		this.categoriaMapper = categoriaMapper;
	}
	
	public CategoriaResponse save(CategoriaRequest categoriaRequest) 
	{
		Categoria categoria = categoriaMapper.toCategoriaEntity(categoriaRequest);
		categoriaRepository.save(categoria);
		
		return categoriaMapper.toCategoriaResponse(categoria);
	}
	
	public List<CategoriaResponse> findAll()
	{
		List<Categoria> categorias = categoriaRepository.findByStatusTrue();
		List<CategoriaResponse> categoriaResponses = new ArrayList<>();
		
		for(int i = 0; i < categorias.size(); i++ ) 
		{
			Categoria categoria = categorias.get(i);
			categoriaResponses.add(categoriaMapper.toCategoriaResponse(categoria));
		}
		
		return categoriaResponses;
	}
	
	public CategoriaResponse updateStatus(Long id) 
	{
		if(id == null || id <= 0) {throw new CategoriaNaoEncontradaException("Informe uma categoria para remover a categoria");}
		
		Categoria categoria = categoriaRepository.findById(id).orElseThrow(()-> new CategoriaNaoEncontradaException("Categoria informada não existe"));
		categoria.setStatus(true);
		
		categoriaRepository.save(categoria);
		
		return categoriaMapper.toCategoriaResponse(categoria);
	}
	
	public CategoriaResponse update(Long id, CategoriaRequest categoriaRequest) 
	{
		if(id == null || id <= 0) {throw new CategoriaNaoEncontradaException("Informe uma categoria para atualizar a categoria");}
		
		if(categoriaRequest == null) {throw new CategoriaNaoEncontradaException("Informe uma categoria válida para realizar a atualização");}
		
		Categoria categoria = categoriaRepository.findById(id).orElseThrow(()-> new CategoriaNaoEncontradaException("Categoria informada não existe"));

		categoria.setNome(categoriaRequest.nome());
		
		categoriaRepository.save(categoria);
		
		return categoriaMapper.toCategoriaResponse(categoria);
	}
	
}
