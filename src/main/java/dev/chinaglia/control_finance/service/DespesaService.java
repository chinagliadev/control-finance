package dev.chinaglia.control_finance.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.dto.response.TotalDespesaCategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;
import dev.chinaglia.control_finance.entitdades.Despesa;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.CategoriaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.DespesaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.UsuarioNaoEncontradoException;
import dev.chinaglia.control_finance.mapstruct.DespesaMapper;
import dev.chinaglia.control_finance.repository.CategoriaRepository;
import dev.chinaglia.control_finance.repository.DespesaRepository;
import dev.chinaglia.control_finance.repository.UsuarioRepository;

@Service
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final CategoriaRepository categoriaRepository;
    private final DespesaMapper despesaMapper;
    private final UsuarioRepository usuarioRepository;

    public DespesaService(
            DespesaRepository despesaRepository,
            CategoriaRepository categoriaRepository,
            DespesaMapper despesaMapper,
            UsuarioRepository usuarioRepository) {

        this.despesaRepository = despesaRepository;
        this.categoriaRepository = categoriaRepository;
        this.despesaMapper = despesaMapper;
        this.usuarioRepository = usuarioRepository;
    }

    public DespesaResponse save(DespesaRequest despesaRequest) {

        if (despesaRequest == null) {
            throw new DespesaNaoEncontradaException(
                    "Informe uma despesa válida");
        }

        Usuario usuario = getUsuarioAutenticado();

        Categoria categoria = categoriaRepository
                .findByIdAndStatusTrueAndUsuarioId(
                        despesaRequest.categoria(),
                        usuario.getId())
                .orElseThrow(() ->
                        new CategoriaNaoEncontradaException(
                                "Categoria informada não existe"));

        Despesa despesa =
                despesaMapper.toDespesaEntity(despesaRequest);

        despesa.setCategoria(categoria);
        despesa.setUsuario(usuario);

        despesaRepository.save(despesa);

        return despesaMapper.toDespesaResponse(despesa);
    }

    public Page<DespesaResponse> findAll(int page, int size) {

        Usuario usuario = getUsuarioAutenticado();

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").ascending());

        Page<Despesa> despesas =
                despesaRepository.findByStatusTrueAndUsuarioId(
                        usuario.getId(),
                        pageable);

        List<DespesaResponse> despesasResponses =
                new ArrayList<>();

        for (Despesa despesa : despesas.getContent()) {
            despesasResponses.add(
                    despesaMapper.toDespesaResponse(despesa));
        }

        return new PageImpl<>(
                despesasResponses,
                pageable,
                despesas.getTotalElements());
    }

    public DespesaResponse updateStatus(Long id) {

        if (id == null || id <= 0) {
            throw new DespesaNaoEncontradaException(
                    "Informe uma despesa para remover a despesa");
        }

        Usuario usuario = getUsuarioAutenticado();

        Despesa despesa = despesaRepository
                .findByIdAndStatusTrueAndUsuarioId(
                        id,
                        usuario.getId())
                .orElseThrow(() ->
                        new DespesaNaoEncontradaException(
                                "Despesa informada não existe"));

        despesa.setStatus(false);

        despesaRepository.save(despesa);

        return despesaMapper.toDespesaResponse(despesa);
    }

    public DespesaResponse update(
            Long id,
            DespesaRequest despesaRequest) {

        if (id == null || id <= 0) {
            throw new DespesaNaoEncontradaException(
                    "Informe uma despesa válida");
        }

        if (despesaRequest == null) {
            throw new DespesaNaoEncontradaException(
                    "Informe os dados da despesa");
        }

        Usuario usuario = getUsuarioAutenticado();

        Despesa despesa = despesaRepository
                .findByIdAndStatusTrueAndUsuarioId(
                        id,
                        usuario.getId())
                .orElseThrow(() ->
                        new DespesaNaoEncontradaException(
                                "Despesa informada não existe"));

        Categoria categoria = categoriaRepository
                .findByIdAndStatusTrueAndUsuarioId(
                        despesaRequest.categoria(),
                        usuario.getId())
                .orElseThrow(() ->
                        new CategoriaNaoEncontradaException(
                                "Categoria informada não existe"));

        despesa.setNome(despesaRequest.nome());
        despesa.setDataVencimento(
                despesaRequest.dataVencimento());
        despesa.setValor(despesaRequest.valor());
        despesa.setDescricao(despesaRequest.descricao());
        despesa.setCategoria(categoria);

        despesaRepository.save(despesa);

        return despesaMapper.toDespesaResponse(despesa);
    }

    public BigDecimal sumDespesas() {

        Usuario usuario = getUsuarioAutenticado();

        return despesaRepository.sumDespesas(
                usuario.getId());
    }

    public List<TotalDespesaCategoriaResponse>
            totalDespesaCategoriaResponse() {

        Usuario usuario = getUsuarioAutenticado();

        return despesaRepository.totalDespesaCategorias(
                usuario.getId());
    }

    private Usuario getUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Usuário não autenticado");
        }

        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            throw new RuntimeException(
                    "Usuário autenticado não possui email");
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário não encontrado"));
    }
}