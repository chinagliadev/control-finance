package dev.chinaglia.control_finance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponse(

        Long id,

        String nome,

        LocalDate dataVencimento,
        
        LocalDate dataPagamento,
        
        LocalDate dataDespesa,

        BigDecimal valor,

        String descricao,

        Boolean aPagar,

        Boolean parcelado,

        Boolean parcelaPaga,
        
        Integer quantidadeParcela,

        Integer numeroParcela,
        
        Integer numeroParcelaEditada,

        Long categoria,

        String categoriaNome,

        Boolean status

) {

}