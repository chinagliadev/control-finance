package dev.chinaglia.control_finance.entitdades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tb_despesas")
public class Despesas implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Instant dataVencimento;
	private BigDecimal valor;
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name="categoria_id")
	private Categoria categoria;
	
	@ManyToOne
	@JoinColumn(name="prioridade_id")
	private Prioridade prioridade;
	
	@ManyToOne
	@JoinColumn(name="usuario_id")
	private Usuario usuario;

	public Despesas() {}
	
	public Despesas(Long id, String nome, Instant dataVencimento, BigDecimal valor, String descricao,
			Categoria categoria, Prioridade prioridade, Usuario usuario) {
		this.id = id;
		this.nome = nome;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.descricao = descricao;
		this.categoria = categoria;
		this.prioridade = prioridade;
		this.usuario = usuario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Instant getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Instant dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Despesas other = (Despesas) obj;
		return Objects.equals(id, other.id);
	}
	
}
