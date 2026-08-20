package dev.chinaglia.control_finance.dto.request;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrarUsuarioRequest(

        @NotEmpty(message = "Nome é obrigatório")
        @Size(min = 3, message="Campo nome deve ter no minimo 3 letras")
        String nome,

        @NotEmpty(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotEmpty(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve possuir pelo menos 6 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.]).+$",
                message = "A senha deve conter letra maiúscula, letra minúscula, número e caractere especial"
        )
        String senha,

        @NotEmpty(message = "CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf

) {
}
