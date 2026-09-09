package dev.chinaglia.control_finance.response;

public class ResponseUtil {
	
	public static <T> ApiResponse<T> sucesso(T dado, String mensagem, String caminho)
	{
		ApiResponse<T> response = new ApiResponse<>();
		response.setDados(dado);
		response.setMensagem(mensagem);
		response.setCaminho(caminho);
		response.setStatus(true);
		return response;
	}
	
}
