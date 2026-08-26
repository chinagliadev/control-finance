package dev.chinaglia.control_finance.exception;

public class DespesaNaoEncontradaException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public DespesaNaoEncontradaException(String msg) 
	{
		super(msg);
	}
	
}
