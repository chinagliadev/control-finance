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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/despesas")
@Tag(name = "Despesas", description = "Gerenciador de Despesas")
public class DespesaController {

    private final DespesaService despesaService;

    public DespesaController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    @GetMapping
    @Operation(summary = "Buscar despesas", description = "Busca as despesas do usuário, permitindo filtrar por mês e utilizar paginação.")
    public ResponseEntity<ApiResponse<Page<DespesaResponse>>> findAll(@RequestParam(value = "mes", required = false) Integer mes, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "6") int size) {
        return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.findAll(page, size, mes), "Despesas buscadas com sucesso", "/despesa"));
    }

    @PostMapping
    @Operation(summary = "Cadastrar despesa", description = "Cadastra uma nova despesa para o usuário.")
    public ResponseEntity<ApiResponse<DespesaResponse>> save(@RequestBody @Valid DespesaRequest despesaRequest) {
        DespesaResponse despesaResponse = despesaService.save(despesaRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(despesaResponse.id()).toUri();

        return ResponseEntity.created(location)
                .body(ResponseUtil.sucesso(despesaResponse, "Despesa salva com sucesso", location.toString()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar status da despesa", description = "Atualiza o status de uma despesa existente utilizando seu identificador.")
    public ResponseEntity<ApiResponse<DespesaResponse>> updateStatus(@PathVariable Long id) {
        DespesaResponse despesaResponse = despesaService.updateStatus(id);

        return ResponseEntity.ok(ResponseUtil.sucesso(despesaResponse, "Despesa atualizada com sucesso", "/despesa"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar despesa", description = "Atualiza os dados de uma despesa existente.")
    public ResponseEntity<ApiResponse<DespesaResponse>> update(@PathVariable Long id, @RequestBody @Valid DespesaRequest despesaRequest) {
        return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.update(id, despesaRequest), "Despesa atualizada com sucesso", "/despesa"));
    }

    @GetMapping("/total")
    @Operation(summary = "Calcular total de despesas", description = "Calcula o valor total das despesas do usuário, podendo filtrar os resultados por mês.")
    public ResponseEntity<ApiResponse<BigDecimal>> sumDespesas(@RequestParam(value = "mes", required = false) Integer mes) {
        BigDecimal total = despesaService.sumDespesas(mes);

        return ResponseEntity.ok(ResponseUtil.sucesso(total, "Total de despesas calculado com sucesso", "/despesas/total"));
    }

    @GetMapping("/totalCategoriaDespesas")
    @Operation(summary = "Calcular despesas por categoria", description = "Calcula o total das despesas agrupadas por categoria, podendo filtrar os resultados por mês.")
    public ResponseEntity<ApiResponse<List<TotalDespesaCategoriaResponse>>> totalDespesaCategoriaResponse(@RequestParam(value = "mes", required = false) Integer mes) {
        return ResponseEntity.ok(ResponseUtil.sucesso(despesaService.totalDespesaCategoriaResponse(mes), "Total de despesas calculado com sucesso", "/despesas/total"));
    }
}