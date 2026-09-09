package dev.chinaglia.control_finance.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.dto.request.PrioridadeRequest;
import dev.chinaglia.control_finance.dto.response.PrioridadeResponse;
import dev.chinaglia.control_finance.response.ApiResponse;
import dev.chinaglia.control_finance.response.ResponseUtil;
import dev.chinaglia.control_finance.service.PrioridadeService;



@RestController
@RequestMapping("/prioridades")
public class PrioridadeController {
	
	private final PrioridadeService prioridadeService;
	
	public PrioridadeController(PrioridadeService prioridadeService) 
	{
		this.prioridadeService = prioridadeService;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<PrioridadeResponse>> save(@RequestBody PrioridadeRequest prioridadeRequest) {

	    PrioridadeResponse prioridadeResponse = prioridadeService.save(prioridadeRequest);

	    URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(prioridadeResponse.id()).toUri();

	    return ResponseEntity.created(location).body(ResponseUtil.sucesso(prioridadeResponse,"Prioridade cadastrada com sucesso",location.toString()));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<PrioridadeResponse>>> findAll() 
	{
		return ResponseEntity.ok(ResponseUtil.sucesso(prioridadeService.findAll(), "Prioridades buscadas com sucesso", "/prioridades"));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<PrioridadeResponse>> updateStatus(@PathVariable Long id)
	{
		return ResponseEntity.ok(ResponseUtil.sucesso(prioridadeService.updateStatus(id), "Prioridade editada com sucesso", "/prioridades/id") );
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PrioridadeResponse>> update(@PathVariable Long id, @RequestBody PrioridadeRequest prioridadeRequest)
	{
		return ResponseEntity.ok().body(ResponseUtil.sucesso(prioridadeService.update(id, prioridadeRequest), "Prioridade editada com sucesso", "/prioridades/id"));
	}
	
}
