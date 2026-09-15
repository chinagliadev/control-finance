package dev.chinaglia.control_finance.specification;

import org.springframework.data.jpa.domain.Specification;

import dev.chinaglia.control_finance.entitdades.Despesa;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public class DespesaSpecification {
	
	/**
	 * Metodo que filtra a despesa pelo mês
	 * @param mes
	 * @return Specification<Despesa>
	 */
	public static Specification<Despesa> temMes(Integer mes) 
	{

	    return (root, query, criteriaBuilder) -> {

	        if (mes == null) {
	            return null;
	        }

	        Expression<Integer> mesDaData =
	                criteriaBuilder.function(
	                        "MONTH",
	                        Integer.class,
	                        root.get("dataVencimento")
	                );

	        Predicate filtroMes =criteriaBuilder.equal(mesDaData, mes);

	        return filtroMes;
	    };
	}
	
	public static Specification<Despesa> usuarioId(Long usuarioId) {
	    return (root, query, cb) -> usuarioId == null ? null : cb.equal(root.get("usuario").get("id"), usuarioId);
	}
	
	public static Specification<Despesa> statusTrue()
	{
		return (root, query, cb) -> cb.isTrue(root.get("status"));
	}
	
	
}
