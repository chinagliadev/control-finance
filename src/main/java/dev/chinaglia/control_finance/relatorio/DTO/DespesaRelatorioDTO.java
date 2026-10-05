package dev.chinaglia.control_finance.relatorio.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DespesaRelatorioDTO {

	private String nome;
	private String categoria;
	private Integer quantidade_parcelada;
	private String data;
	private BigDecimal valor;
	private LocalDate dataRef; 
	private String situacao;

	public DespesaRelatorioDTO() {};

	public DespesaRelatorioDTO(String nome, String categoria, Integer quantidade_parcelada, String data, BigDecimal valor, String situacao) {
		this.nome = nome;
		this.categoria = categoria;
		this.quantidade_parcelada = quantidade_parcelada;
		this.data = data;
		this.valor = valor;
		this.situacao = situacao;
	}

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }

	public Integer getQuantidade_parcelada() { return quantidade_parcelada; }
	public void setQuantidade_parcelada(Integer quantidade_parcelada) { this.quantidade_parcelada = quantidade_parcelada; }

	public String getData() { return data; }
	public void setData(String data) { this.data = data; }

	public BigDecimal getValor() { return valor; }
	public void setValor(BigDecimal valor) { this.valor = valor; }

	public LocalDate getDataRef() { return dataRef; }
	public void setDataRef(LocalDate dataRef) { this.dataRef = dataRef; }

	public String getSituacao() {return situacao;}
	public void setSituacao(String situacao) {this.situacao = situacao;}
}