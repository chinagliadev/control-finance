package dev.chinaglia.control_finance.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.chinaglia.control_finance.dto.response.TotalDespesaCategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Despesa;

@Repository
public interface DespesaRepository
        extends JpaRepository<Despesa, Long>,
                JpaSpecificationExecutor<Despesa> {

    Page<Despesa> findByStatusTrueAndUsuarioId(
            Long usuarioId,
            Pageable pageable
    );

    Optional<Despesa> findByIdAndStatusTrueAndUsuarioId(
            Long id,
            Long usuarioId
    );

    /**
     * Soma o total de despesas do usuário no mês informado
     * ou geral, se mês for null.
     */
    @Query(value = """
            SELECT COALESCE(SUM(total), 0)
            FROM (
                SELECT tb_parcelas.valor AS total
                FROM tb_parcelas
                JOIN tb_despesas
                    ON tb_despesas.id = tb_parcelas.despesa_id
                WHERE tb_parcelas.status != 0
                AND tb_despesas.status != 0
                AND tb_despesas.parcelado = 1
                AND tb_despesas.usuario_id = :usuarioId
                AND (
                    :mes IS NULL
                    OR MONTH(tb_parcelas.data_vencimento) = :mes
                )

                UNION ALL

                SELECT tb_despesas.valor AS total
                FROM tb_despesas
                WHERE tb_despesas.status != 0
                AND tb_despesas.parcelado = 0
                AND tb_despesas.usuario_id = :usuarioId
                AND (
                    :mes IS NULL
                    OR MONTH(tb_despesas.data_despesa) = :mes
                )
            ) AS uniao
            """, nativeQuery = true)
    BigDecimal sumDespesas(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") Integer mes
    );

    /**
     * Total de despesas agrupado por categoria.
     */
    @Query(value = """
            SELECT categoria,
                   COALESCE(SUM(total), 0) AS total
            FROM (
                SELECT tb_categoria.nome AS categoria,
                       tb_parcelas.valor AS total
                FROM tb_parcelas
                JOIN tb_despesas
                    ON tb_despesas.id = tb_parcelas.despesa_id
                JOIN tb_categoria
                    ON tb_categoria.id = tb_despesas.categoria_id
                WHERE tb_parcelas.status != 0
                AND tb_despesas.status != 0
                AND tb_despesas.parcelado = 1
                AND tb_despesas.usuario_id = :usuarioId
                AND (
                    :mes IS NULL
                    OR MONTH(tb_parcelas.data_vencimento) = :mes
                )

                UNION ALL

                SELECT tb_categoria.nome AS categoria,
                       tb_despesas.valor AS total
                FROM tb_despesas
                JOIN tb_categoria
                    ON tb_categoria.id = tb_despesas.categoria_id
                WHERE tb_despesas.status != 0
                AND tb_despesas.parcelado = 0
                AND tb_despesas.usuario_id = :usuarioId
                AND (
                    :mes IS NULL
                    OR MONTH(tb_despesas.data_despesa) = :mes
                )
            ) AS uniao
            GROUP BY categoria
            ORDER BY total DESC
            LIMIT 10
            """, nativeQuery = true)
    List<TotalDespesaCategoriaResponse> totalDespesaCategorias(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") Integer mes
    );

    /**
     * Busca despesas que não possuem parcelas.
     */
    @Query("""
            SELECT d
            FROM Despesa d
            WHERE d.usuario.id = :usuarioId
              AND d.status = true
              AND d.parcelas IS EMPTY
              AND (
                  :mes IS NULL
                  OR MONTH(d.dataDespesa) = :mes
              )
            ORDER BY d.dataDespesa ASC
            """)
    List<Despesa> buscarDespesasNaoParceladas(
            @Param("usuarioId") Long usuarioId,
            @Param("mes") Integer mes
    );
}