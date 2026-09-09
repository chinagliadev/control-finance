package dev.chinaglia.control_finance.response;

public class ApiResponse<T> {
	
	private Boolean status;
	private String mensagem;
	private T dados;
	private String caminho;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public T getDados() {
		return dados;
	}
	public void setDados(T dados) {
		this.dados = dados;
	}
	public String getCaminho() {
		return caminho;
	}
	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
}
