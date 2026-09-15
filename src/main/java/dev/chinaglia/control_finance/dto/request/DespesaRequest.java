package dev.chinaglia.control_finance.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DespesaRequest(

		@NotBlank(message = "Informe o nome da despesa")
		String nome,

		LocalDate dataDespesa,

		@FutureOrPresent(message = "A data de vencimento não pode ser anterior a hoje")
		LocalDate dataVencimento,

		@NotNull(message = "Informe o valor da despesa")
		@Positive(message = "O valor da despesa deve ser maior que zero")
		BigDecimal valor,

		@NotBlank(message = "Informe a descrição da despesa")
		String descricao,

		@NotNull(message = "Informe se a despesa está a pagar")
		Boolean aPagar,

		Boolean parcelado,

		@Positive(message = "A quantidade de parcelas deve ser maior que zero")
		Integer quantidadeParcela,

		@NotNull(message = "Informe a categoria da despesa")
		@Positive(message = "A categoria informada deve ser válida")
		Long categoria

) {

}