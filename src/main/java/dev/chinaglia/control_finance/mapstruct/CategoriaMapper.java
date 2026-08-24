package dev.chinaglia.control_finance.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.chinaglia.control_finance.dto.request.CategoriaRequest;
import dev.chinaglia.control_finance.dto.response.CategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", expression = "java(categoriaRequest.status() != null ? categoriaRequest.status() : true)")
	Categoria toCategoriaEntity(CategoriaRequest categoriaRequest);

	CategoriaResponse toCategoriaResponse(Categoria categoria);
	
}
