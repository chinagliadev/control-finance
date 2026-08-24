package dev.chinaglia.control_finance.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record CategoriaRequest(
        @NotEmpty(message = "Nome é obrigatório")
        String nome,

        Boolean status
) {
    
    public CategoriaRequest(String nome) {
        this(nome, false);
    }
}
