package dev.chinaglia.control_finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.entitdades.Parcela;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    List<Parcela> findByDespesaId(Long despesaId);

    Optional<Parcela> findByDespesaIdAndNumeroParcela(Long despesaId, Integer numeroParcela);

    @Query("""
            SELECT p
            FROM Parcela p
            JOIN FETCH p.despesa d
            WHERE d.usuario.id = :usuarioId
              AND d.status = true
              AND p.status = true
            """)
    List<Parcela> buscarPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT p
            FROM Parcela p
            JOIN FETCH p.despesa d
            WHERE d.usuario.id = :usuarioId
              AND d.status = true
              AND p.status = true
              AND (
                  :mes IS NULL
                  OR MONTH(p.dataVencimento) = :mes
              )
            """)
    List<Parcela> buscarPorUsuarioEMes(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") Integer mes
    );
}