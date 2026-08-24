package dev.chinaglia.control_finance.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record PrioridadeRequest
	(
		@NotEmpty(message="Nome é obrigatório")
		String nome, 
		@NotEmpty(message="Cor é obrigatório")
		String cor,
		
		Boolean status
	) {

	public PrioridadeRequest(String nome, String cor) {
		 this(nome, cor, false);
	}
	
}
