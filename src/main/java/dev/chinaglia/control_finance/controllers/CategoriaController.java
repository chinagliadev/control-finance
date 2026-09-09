package dev.chinaglia.control_finance.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.dto.request.CategoriaRequest;
import dev.chinaglia.control_finance.dto.response.CategoriaResponse;
import dev.chinaglia.control_finance.response.ApiResponse;
import dev.chinaglia.control_finance.response.ResponseUtil;
import dev.chinaglia.control_finance.service.CategoriaService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/categorias")
public class CategoriaController {
	
	private final CategoriaService categoriaService;
	
	public CategoriaController(CategoriaService categoriaService) 
	{
		this.categoriaService = categoriaService;
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoriaResponse>>> findAll()
	{
		return ResponseEntity.ok(ResponseUtil.sucesso(categoriaService.findAll(), "Categorias buscadas com sucesso", "/categorias"));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<CategoriaResponse>> save(@RequestBody @Valid CategoriaRequest categoriaRequest) 
	{
		CategoriaResponse categoriaResponse = categoriaService.save(categoriaRequest);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoriaResponse.id()).toUri();
		return ResponseEntity.created(location).body(ResponseUtil.sucesso(categoriaResponse, "Categoria salva com sucesso", location.toString()));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoriaResponse>> updateStatus(@PathVariable Long id) {
	    CategoriaResponse categoriaResponse = categoriaService.updateStatus(id);
	    return ResponseEntity.ok(ResponseUtil.sucesso(categoriaResponse, "Categoria atualizada com sucesso", "/categorias"));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoriaResponse>> update(@PathVariable Long id, @RequestBody @Valid CategoriaRequest categoriaRequest)
	{
		return ResponseEntity.ok().body(ResponseUtil.sucesso(categoriaService.update(id, categoriaRequest), "Categoria ", "categorias"));
	}
}
