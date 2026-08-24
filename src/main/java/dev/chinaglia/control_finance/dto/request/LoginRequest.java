package dev.chinaglia.control_finance.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest
		(
				@NotEmpty(message="Email é obrigatório")
				String email, 
				@NotEmpty(message="Senha é obrigatória")
				String senha
				
				
		) {

}
