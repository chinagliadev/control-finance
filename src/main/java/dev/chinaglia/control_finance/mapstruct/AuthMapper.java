package dev.chinaglia.control_finance.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.chinaglia.control_finance.dto.request.RegistrarUsuarioRequest;
import dev.chinaglia.control_finance.dto.response.LoginResponse;
import dev.chinaglia.control_finance.dto.response.RegistrarUsuarioResponse;
import dev.chinaglia.control_finance.entitdades.Usuario;

@Mapper(componentModel = "spring")
public interface AuthMapper {

	@Mapping(target = "id", ignore = true)
	Usuario toUsuarioEntity(RegistrarUsuarioRequest registrarUsuarioRequest);
	
	LoginResponse toLoginResponse(Usuario usuario);

	RegistrarUsuarioResponse toRegistrarUsuarioResponse (Usuario usuario);
	                        
	
}
