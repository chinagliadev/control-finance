package dev.chinaglia.control_finance.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DespesaResponse
	(
		Long id,
		String nome,
		LocalDate dataVencimento,
		BigDecimal valor,
		String descricao,
		CategoriaResponse categoria

	) {

}
