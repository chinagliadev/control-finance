package dev.chinaglia.control_finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.entitdades.Despesa;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long>{
	
	List<Despesa> findByStatusTrue();
	
	 Optional<Despesa> findByIdAndStatusTrue(Long id);
	
}
