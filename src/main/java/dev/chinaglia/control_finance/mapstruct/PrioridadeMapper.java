package dev.chinaglia.control_finance.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.chinaglia.control_finance.dto.request.PrioridadeRequest;
import dev.chinaglia.control_finance.dto.response.PrioridadeResponse;
import dev.chinaglia.control_finance.entitdades.Prioridade;

@Mapper(componentModel = "spring")
public interface PrioridadeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", expression = "java(prioridadeRequest.status() != null ? prioridadeRequest.status() : true)")
	Prioridade toPrioridadeEntity(PrioridadeRequest prioridadeRequest);

	PrioridadeResponse toPrioridadeResponse(Prioridade prioridade);
}