package dev.chinaglia.control_finance.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.dto.request.PrioridadeRequest;
import dev.chinaglia.control_finance.dto.response.PrioridadeResponse;
import dev.chinaglia.control_finance.entitdades.Prioridade;
import dev.chinaglia.control_finance.exception.PrioridadeNaoEncontradaException;
import dev.chinaglia.control_finance.mapstruct.PrioridadeMapper;
import dev.chinaglia.control_finance.repository.PrioridadeRepository;

@Service
public class PrioridadeService {

	private final PrioridadeRepository prioridadeRepository;
	private final PrioridadeMapper prioridadeMapper;
	
	public PrioridadeService(PrioridadeRepository prioridadeRepository, PrioridadeMapper prioridadeMapper) 
	{
		this.prioridadeRepository = prioridadeRepository;
		this.prioridadeMapper = prioridadeMapper;
	}
	
	public PrioridadeResponse save(PrioridadeRequest prioridadeRequest) 
	{
		if(prioridadeRequest == null) {throw new PrioridadeNaoEncontradaException("Informe uma prioridade para salvar");}

		Prioridade prioridade = prioridadeMapper.toPrioridadeEntity(prioridadeRequest);
		prioridadeRepository.save(prioridade);
		
		return prioridadeMapper.toPrioridadeResponse(prioridade);
	}
	
	public List<PrioridadeResponse> findAll()
	{
		List<Prioridade> prioridades = prioridadeRepository.findByStatusTrue();
		
		List<PrioridadeResponse> prioridadesResponses = new ArrayList<>();
		
		for(int i = 0; i < prioridades.size(); i++) 
		{
			Prioridade prioridade = prioridades.get(i);
			
			prioridadesResponses.add(prioridadeMapper.toPrioridadeResponse(prioridade));
		}
		
		return prioridadesResponses;
	}
	
	public PrioridadeResponse updateStatus(Long id) {

	    if (id == null || id <= 0) {
	        throw new PrioridadeNaoEncontradaException(
	            "Informe uma prioridade valida"
	        );
	    }

	    Prioridade prioridade = prioridadeRepository.findById(id).orElseThrow(() -> new PrioridadeNaoEncontradaException("Prioridade informada não existe, informe uma prioridade novamente"));

	    prioridadeRepository.desativarPrioridade(id);

	    prioridade.setStatus(false);

	    return prioridadeMapper.toPrioridadeResponse(prioridade);
	}
	
}
