package dev.chinaglia.control_finance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(
		Long id,
		String nome,
		LocalDate dataDespesa,
		LocalDate dataVencimento,
		BigDecimal valor,
		String descricao,
		boolean aPagar,
		boolean parcelado,
		Integer quantidadeParcela,
		CategoriaResponse categoria
) {

}