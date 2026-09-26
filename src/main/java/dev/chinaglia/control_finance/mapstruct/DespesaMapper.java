package dev.chinaglia.control_finance.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.entitdades.Despesa;
import dev.chinaglia.control_finance.entitdades.Parcela;

@Mapper(componentModel = "spring")
public interface DespesaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "status", ignore = true)
    Despesa toDespesaEntity(DespesaRequest request);


    // Despesa sem parcela
    @Mapping(target = "categoria", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    @Mapping(target = "parcelaPaga", ignore = true)
    @Mapping(target = "numeroParcela", ignore = true)
    @Mapping(target = "numeroParcelaEditada", ignore = true)
    DespesaResponse toDespesaResponse(Despesa despesa);


    // Despesa com parcela
    @Mapping(target = "id", source = "despesa.id")
    @Mapping(target = "nome", source = "despesa.nome")
    @Mapping(target = "dataVencimento", source = "parcela.dataVencimento")
    @Mapping(target = "dataPagamento", source = "parcela.dataPagamento")
    @Mapping(target = "dataDespesa", source = "despesa.dataDespesa")
    @Mapping(target = "valor", source = "parcela.valor")
    @Mapping(target = "descricao", source = "despesa.descricao")
    @Mapping(target = "aPagar", source = "despesa.aPagar")
    @Mapping(target = "parcelado", source = "despesa.parcelado")
    @Mapping(target = "parcelaPaga", source = "parcela.parcelaPaga")
    @Mapping(target = "quantidadeParcela", source = "despesa.quantidadeParcela")
    @Mapping(target = "numeroParcela", source = "parcela.numeroParcela")
    @Mapping(target = "numeroParcelaEditada", ignore = true)
    @Mapping(target = "categoria", source = "despesa.categoria.id")
    @Mapping(target = "categoriaNome", source = "despesa.categoria.nome")
    @Mapping(target = "status", source = "despesa.status")
    DespesaResponse toDespesaResponse(
            Despesa despesa,
            Parcela parcela
    );
}
