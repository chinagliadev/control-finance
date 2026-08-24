package dev.chinaglia.control_finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.entitdades.Prioridade;
import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface PrioridadeRepository extends JpaRepository<Prioridade, Long>{

	List<Prioridade> findByStatusTrue();
	
	@Modifying
    @Transactional
    @Query("UPDATE Prioridade p SET p.status = false WHERE p.id = :id")
    void desativarPrioridade(@Param("id") Long id);
}
