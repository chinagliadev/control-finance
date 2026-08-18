package dev.chinaglia.control_finance.entitdades;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="tb_prioridade")
public class Prioridade implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String cor;
	private Boolean status = true;
	
	@OneToMany(mappedBy = "prioridade", fetch = FetchType.LAZY)
	public Set<Despesa> despesas = new HashSet<>();
	
	public Prioridade() {}
	
	public Prioridade(Long id, String nome, String cor, Boolean status, Set<Despesa> despesas) {
		this.id = id;
		this.nome = nome;
		this.cor = cor;
		this.status = status;
		this.despesas = despesas;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Set<Despesa> getDespesas() {
		return despesas;
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
		Prioridade other = (Prioridade) obj;
		return Objects.equals(id, other.id);
	}
	
}
