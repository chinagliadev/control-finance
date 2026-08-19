package dev.chinaglia.control_finance.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegistrarUsuarioRequest
	(
			@NotEmpty(message="Nome é obrigatório")
			String nome, 
			@NotEmpty(message="Email é obrigatorio")
			String email,
			@NotEmpty(message="Senha é obrigatorio")
			String senha,
			@NotEmpty(message="CPF é obrigatorio")
			String cpf
	) {

}
