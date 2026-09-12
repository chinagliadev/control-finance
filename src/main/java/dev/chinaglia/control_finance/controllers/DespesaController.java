package dev.chinaglia.control_finance.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.dto.response.TotalDespesaCategoriaResponse;
import dev.chinaglia.control_finance.response.ApiResponse;
import dev.chinaglia.control_finance.response.ResponseUtil;
import dev.chinaglia.control_finance.service.DespesaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/despesas")
public class DespesaController {

	private final DespesaService despesaService;

	public DespesaController(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<DespesaResponse>>> findAll(
			@RequestParam(value="page", defaultValue = "0") int page, 
			@RequestParam(value = "size", defaultValue = "6") int size
	) {
		
		return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.findAll(page, size), "Despesas buscadas com sucesso", "/despesa"));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<DespesaResponse>> save(@RequestBody @Valid DespesaRequest despesaRequest) {

		DespesaResponse despesaResponse = despesaService.save(despesaRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(despesaResponse.id()).toUri();

		return ResponseEntity.created(location)
				.body(ResponseUtil.sucesso(despesaResponse, "Despesa salva com sucesso", location.toString()));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<DespesaResponse>> updateStatus(@PathVariable Long id) {

		DespesaResponse despesaResponse = despesaService.updateStatus(id);

		return ResponseEntity.ok(ResponseUtil.sucesso(despesaResponse, "Despesa atualizada com sucesso", "/despesa"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DespesaResponse>> update(@PathVariable Long id,
			@RequestBody @Valid DespesaRequest despesaRequest) {

		return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.update(id, despesaRequest),
				"Despesa atualizada com sucesso", "/despesa"));
	}
	
	@GetMapping("/total")
	public ResponseEntity<ApiResponse<BigDecimal>> sumDespesas() {
	    BigDecimal total = despesaService.sumDespesas();
	    return ResponseEntity.ok(ResponseUtil.sucesso(total,"Total de despesas calculado com sucesso", "/despesas/total"));
	}
	@GetMapping("/totalCategoriaDespesas")
	public ResponseEntity<ApiResponse<List<TotalDespesaCategoriaResponse>>> totalDespesaCategoriaResponse() {
	    return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.totalDespesaCategoriaResponse(),"Total de despesas calculado com sucesso", "/despesas/total"));
	}
	
}
