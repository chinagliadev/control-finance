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
@Table(name = "tb_despesas")
public class Despesa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDate dataVencimento;

    private LocalDate dataDespesa;

    private BigDecimal valor;

    private String descricao;

    private Boolean aPagar;

    private Boolean parcelado;

    private Integer quantidadeParcela;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Boolean status = true;

    public Despesa() {
    }

    public Despesa(Long id, String nome, LocalDate dataVencimento, LocalDate dataDespesa,
            BigDecimal valor, String descricao, Boolean aPagar, Boolean parcelado,
            Integer quantidadeParcela, Categoria categoria, Usuario usuario) {

        this.id = id;
        this.nome = nome;
        this.dataVencimento = dataVencimento;
        this.dataDespesa = dataDespesa;
        this.valor = valor;
        this.descricao = descricao;
        this.aPagar = aPagar;
        this.parcelado = parcelado;
        this.quantidadeParcela = quantidadeParcela;
        this.categoria = categoria;
        this.usuario = usuario;
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

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataDespesa() {
        return dataDespesa;
    }

    public void setDataDespesa(LocalDate dataDespesa) {
        this.dataDespesa = dataDespesa;
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

    public Boolean getaPagar() {
        return aPagar;
    }

    public void setaPagar(Boolean aPagar) {
        this.aPagar = aPagar;
    }

    public Boolean getParcelado() {
        return parcelado;
    }

    public void setParcelado(Boolean parcelado) {
        this.parcelado = parcelado;
    }

    public Integer getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Integer quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

        Despesa other = (Despesa) obj;

        return Objects.equals(id, other.id);
    }
}