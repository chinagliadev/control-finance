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

import dev.chinaglia.control_finance.dto.request.CategoriaRequest;
import dev.chinaglia.control_finance.dto.response.CategoriaResponse;
import dev.chinaglia.control_finance.response.ApiResponse;
import dev.chinaglia.control_finance.response.ResponseUtil;
import dev.chinaglia.control_finance.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Gerenciador de Categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Buscar categorias", description = "Busca todas as categorias cadastradas para o usuário.")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> findAll() {
        return ResponseEntity.ok(ResponseUtil.sucesso(categoriaService.findAll(), "Categorias buscadas com sucesso", "/categorias"));
    }

    @PostMapping
    @Operation(summary = "Cadastrar categoria", description = "Cadastra uma nova categoria para o usuário.")
    public ResponseEntity<ApiResponse<CategoriaResponse>> save(@RequestBody @Valid CategoriaRequest categoriaRequest) {
        CategoriaResponse categoriaResponse = categoriaService.save(categoriaRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(categoriaResponse.id()).toUri();

        return ResponseEntity.created(location)
                .body(ResponseUtil.sucesso(categoriaResponse, "Categoria salva com sucesso", location.toString()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar status da categoria", description = "Atualiza o status de uma categoria existente utilizando seu identificador.")
    public ResponseEntity<ApiResponse<CategoriaResponse>> updateStatus(@PathVariable Long id) {
        CategoriaResponse categoriaResponse = categoriaService.updateStatus(id);

        return ResponseEntity.ok(ResponseUtil.sucesso(categoriaResponse, "Categoria atualizada com sucesso", "/categorias"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente.")
    public ResponseEntity<ApiResponse<CategoriaResponse>> update(@PathVariable Long id, @RequestBody @Valid CategoriaRequest categoriaRequest) {
        return ResponseEntity.ok().body(ResponseUtil.sucesso(categoriaService.update(id, categoriaRequest), "Categoria ", "categorias"));
    }
}