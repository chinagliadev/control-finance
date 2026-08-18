package dev.chinaglia.control_finance.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	
	@GetMapping
	public String getUsuario()
	{
	    return "rota usuario funcionando";
	}
	
}
