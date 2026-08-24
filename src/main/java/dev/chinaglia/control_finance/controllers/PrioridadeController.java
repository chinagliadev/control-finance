package dev.chinaglia.control_finance.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.dto.request.PrioridadeRequest;
import dev.chinaglia.control_finance.dto.response.PrioridadeResponse;
import dev.chinaglia.control_finance.service.PrioridadeService;



@RestController
@RequestMapping("/prioridade")
public class PrioridadeController {
	
	private final PrioridadeService prioridadeService;
	
	public PrioridadeController(PrioridadeService prioridadeService) 
	{
		this.prioridadeService = prioridadeService;
	}
	
	@PostMapping
	public ResponseEntity<PrioridadeResponse> save(@RequestBody PrioridadeRequest prioridadeRequest)
	{
		PrioridadeResponse prioridadeResponse = prioridadeService.save(prioridadeRequest);
		URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(prioridadeResponse.id()).toUri();
		return ResponseEntity.created(location).body(prioridadeResponse);
	}
	
	@GetMapping
	public ResponseEntity<List<PrioridadeResponse>> findAll() 
	{
		return ResponseEntity.ok(prioridadeService.findAll());
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<PrioridadeResponse> updateStatus(@PathVariable Long id)
	{
		return ResponseEntity.ok(prioridadeService.updateStatus(id));
	}
	
}
