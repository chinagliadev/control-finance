package dev.chinaglia.control_finance.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.chinaglia.control_finance.service.DespesaRelatorioService;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

	private final DespesaRelatorioService despesaRelatorioService;

	public RelatorioController(DespesaRelatorioService despesaRelatorioService) {
		this.despesaRelatorioService = despesaRelatorioService;
	}

	@GetMapping("/despesas")
	public ResponseEntity<byte[]> despesas(@RequestParam(value ="mes", required = false) Integer mes) throws Exception {

		byte[] pdf = despesaRelatorioService.gerarRelatorio(mes);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=despesas.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}
}