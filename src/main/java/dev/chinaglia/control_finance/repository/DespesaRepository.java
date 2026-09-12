package dev.chinaglia.control_finance.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.dto.response.TotalDespesaCategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Despesa;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    Page<Despesa> findByStatusTrueAndUsuarioId(
            Long usuarioId,
            Pageable pageable);

    Optional<Despesa> findByIdAndStatusTrueAndUsuarioId(
            Long id,
            Long usuarioId);

    @Query(value = """
            SELECT COALESCE(SUM(valor), 0)
            FROM tb_despesas
            WHERE status != 0
            AND usuario_id = :usuarioId
            """, nativeQuery = true)
    BigDecimal sumDespesas(
            @Param("usuarioId") Long usuarioId);

    @Query(value = """
            SELECT tb_categoria.nome AS categoria,
                   COALESCE(SUM(tb_despesas.valor), 0) AS total
            FROM tb_despesas
            JOIN tb_categoria
                ON tb_categoria.id = tb_despesas.categoria_id
            WHERE tb_despesas.status != 0
            AND tb_despesas.usuario_id = :usuarioId
            GROUP BY tb_categoria.nome
            ORDER BY total DESC
            LIMIT 10
            """, nativeQuery = true)
    List<TotalDespesaCategoriaResponse> totalDespesaCategorias(
            @Param("usuarioId") Long usuarioId);
}