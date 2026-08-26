package dev.chinaglia.control_finance.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.entitdades.Despesa;

@Mapper(componentModel = "spring")
public interface DespesaMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "categoria", ignore = true)
	@Mapping(target = "prioridade", ignore = true)
	@Mapping(target = "usuario", ignore = true)
	@Mapping(target = "status", ignore = true)
	Despesa toDespesaEntity(DespesaRequest request);
	
	DespesaResponse toDespesaResponse(Despesa despesa);
}
