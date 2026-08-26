package dev.chinaglia.control_finance.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.service.DespesaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/despesa")
public class DespesaController {

	private final DespesaService despesaService;
	
    public DespesaController(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

    @PostMapping
    public ResponseEntity<DespesaResponse> save(@RequestBody @Valid DespesaRequest despesaRequest) {
    	DespesaResponse despesasResponse = despesaService.save(despesaRequest);
    	URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{}").buildAndExpand(despesasResponse.id()).toUri();
        return ResponseEntity.created(location).body(despesasResponse);
    }
    
    @GetMapping
    public ResponseEntity<List<DespesaResponse>> findAll() {
        return ResponseEntity.ok(despesaService.findAll());
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<DespesaResponse> updateStatus(@PathVariable Long id) {

        DespesaResponse despesaResponse = despesaService.updateStatus(id);

        return ResponseEntity.ok(despesaResponse);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid DespesaRequest despesaRequest) {

        DespesaResponse despesaResponse = despesaService.update(id, despesaRequest);

        return ResponseEntity.ok(despesaResponse);
    }
    
    
    
}
