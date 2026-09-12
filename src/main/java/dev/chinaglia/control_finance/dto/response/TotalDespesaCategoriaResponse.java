package dev.chinaglia.control_finance.dto.response;

import java.math.BigDecimal;

public record TotalDespesaCategoriaResponse(
	    String categoria,
	    BigDecimal total
	) {}