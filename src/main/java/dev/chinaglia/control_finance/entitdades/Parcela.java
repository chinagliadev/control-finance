package dev.chinaglia.control_finance.entitdades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_parcelas")
public class Parcela implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroParcela;

    private BigDecimal valor;

    private LocalDate dataVencimento;

    private Boolean status = true;
    
    private Boolean parcelaPaga = false;
    
    private LocalDate dataPagamento;
    
    @ManyToOne
    @JoinColumn(name = "despesa_id")
    private Despesa despesa;

    public Parcela() {
    }

    public Parcela(Long id, Integer numeroParcela, BigDecimal valor,
            LocalDate dataVencimento, LocalDate dataPagamento, Boolean status, Boolean parcelaPaga,
            Despesa despesa) {

        this.id = id;
        this.numeroParcela = numeroParcela;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.parcelaPaga = parcelaPaga;
        this.dataPagamento = dataPagamento;
        this.despesa = despesa;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(Integer numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Despesa getDespesa() {
        return despesa;
    }

    public void setDespesa(Despesa despesa) {
        this.despesa = despesa;
    }

    public Boolean getParcelaPaga() {
        return parcelaPaga;
    }

    public void setParcelaPaga(Boolean parcelaPaga) {
        this.parcelaPaga = parcelaPaga;
    }
    
    public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
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

        Parcela other = (Parcela) obj;

        return Objects.equals(id, other.id);
    }
}