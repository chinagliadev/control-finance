package dev.chinaglia.control_finance.exception;

public class UsuarioNaoEncontradoException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public UsuarioNaoEncontradoException(String msg) 
	{
		super(msg);
	}
	
}
