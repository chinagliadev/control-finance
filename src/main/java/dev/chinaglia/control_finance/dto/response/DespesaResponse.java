package dev.chinaglia.control_finance.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record DespesaResponse
	(
		Long id,
		String nome,
		Instant dataVencimento,
		BigDecimal valor,
		String descricao,
		CategoriaResponse categoria,
		PrioridadeResponse prioridade
		
	) {

}
