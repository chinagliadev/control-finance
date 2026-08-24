package dev.chinaglia.control_finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.entitdades.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
	
	List<Categoria> findByStatusTrue();
	
	Optional<Categoria> findByIdAndStatusTrue(Long id);
}
