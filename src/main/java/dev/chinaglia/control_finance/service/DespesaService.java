package dev.chinaglia.control_finance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dev.chinaglia.control_finance.dto.request.DespesaRequest;
import dev.chinaglia.control_finance.dto.response.DespesaResponse;
import dev.chinaglia.control_finance.dto.response.TotalDespesaCategoriaResponse;
import dev.chinaglia.control_finance.entitdades.Categoria;
import dev.chinaglia.control_finance.entitdades.Despesa;
import dev.chinaglia.control_finance.entitdades.Parcela;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.CategoriaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.ControlFinanceException;
import dev.chinaglia.control_finance.exception.DespesaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.UsuarioNaoEncontradoException;
import dev.chinaglia.control_finance.mapstruct.DespesaMapper;
import dev.chinaglia.control_finance.repository.CategoriaRepository;
import dev.chinaglia.control_finance.repository.DespesaRepository;
import dev.chinaglia.control_finance.repository.ParcelaRepository;
import dev.chinaglia.control_finance.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class DespesaService {

	private final DespesaRepository despesaRepository;
	private final CategoriaRepository categoriaRepository;
	private final DespesaMapper despesaMapper;
	private final UsuarioRepository usuarioRepository;
	private final ParcelaRepository parcelaRepository;

	public DespesaService(DespesaRepository despesaRepository, CategoriaRepository categoriaRepository,
			DespesaMapper despesaMapper, UsuarioRepository usuarioRepository, ParcelaRepository parcelaRepository) {
		this.despesaRepository = despesaRepository;
		this.categoriaRepository = categoriaRepository;
		this.despesaMapper = despesaMapper;
		this.usuarioRepository = usuarioRepository;
		this.parcelaRepository = parcelaRepository;
	}

	@Transactional
	public DespesaResponse save(DespesaRequest request) {
		if (request == null) {
			throw new ControlFinanceException("Informe os dados da despesa");
		}

		Usuario usuario = getUsuarioAutenticado();

		Categoria categoria = categoriaRepository
				.findByIdAndStatusTrueAndUsuarioId(request.categoria(), usuario.getId())
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe"));

		Despesa despesa = despesaMapper.toDespesaEntity(request);
		despesa.setUsuario(usuario);
		despesa.setCategoria(categoria);
		despesa.setStatus(true);
		despesa.setDataDespesa(request.dataDespesa() != null ? request.dataDespesa() : LocalDate.now());
		despesa.setaPagar(Boolean.TRUE.equals(request.aPagar()));
		despesa.setParcelado(Boolean.TRUE.equals(request.parcelado()));

		if (Boolean.TRUE.equals(despesa.getParcelado())) {
			if (request.quantidadeParcela() == null || request.quantidadeParcela() <= 0) {
				throw new ControlFinanceException("Informe uma quantidade de parcelas válida");
			}

			despesa.setQuantidadeParcela(request.quantidadeParcela());
		} else {
			despesa.setQuantidadeParcela(null);
		}

		despesaRepository.save(despesa);

		if (Boolean.TRUE.equals(despesa.getParcelado())) {
			criarParcelas(despesa, Boolean.TRUE.equals(request.parcelaPaga()));
		}

		return despesaMapper.toDespesaResponse(despesa);
	}

	private void criarParcelas(Despesa despesa, boolean primeiraParcelaPaga) {
		if (despesa.getQuantidadeParcela() == null || despesa.getQuantidadeParcela() <= 0) {
			throw new ControlFinanceException("Quantidade de parcelas inválida");
		}

		if (despesa.getDataVencimento() == null) {
			throw new ControlFinanceException("Informe a data de vencimento");
		}

		BigDecimal valorParcela = despesa.getValor().divide(BigDecimal.valueOf(despesa.getQuantidadeParcela()), 2,
				RoundingMode.HALF_UP);

		for (int numero = 1; numero <= despesa.getQuantidadeParcela(); numero++) {
			Parcela parcela = new Parcela();
			parcela.setNumeroParcela(numero);
			parcela.setValor(valorParcela);
			parcela.setDataVencimento(despesa.getDataVencimento().plusMonths(numero - 1L));
			parcela.setStatus(true);

			boolean paga = numero == 1 && primeiraParcelaPaga;
			parcela.setParcelaPaga(paga);
			parcela.setDataPagamento(paga ? LocalDate.now() : null);
			parcela.setDespesa(despesa);

			parcelaRepository.save(parcela);
		}

		BigDecimal soma = parcelaRepository.findByDespesaId(despesa.getId()).stream().map(Parcela::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal diferenca = despesa.getValor().subtract(soma);

		if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
			List<Parcela> parcelas = parcelaRepository.findByDespesaId(despesa.getId());

			Parcela ultima = parcelas.stream().max(Comparator.comparing(Parcela::getNumeroParcela)).orElseThrow();

			ultima.setValor(ultima.getValor().add(diferenca));
			parcelaRepository.save(ultima);
		}
	}

	@Transactional
	public DespesaResponse update(Long id, DespesaRequest request) {
		if (id == null || id <= 0) {
			throw new DespesaNaoEncontradaException("Informe uma despesa válida");
		}

		if (request == null) {
			throw new ControlFinanceException("Informe os dados da despesa");
		}

		Usuario usuario = getUsuarioAutenticado();

		Despesa despesa = despesaRepository.findByIdAndStatusTrueAndUsuarioId(id, usuario.getId())
				.orElseThrow(() -> new DespesaNaoEncontradaException("Despesa informada não existe"));

		Categoria categoria = categoriaRepository
				.findByIdAndStatusTrueAndUsuarioId(request.categoria(), usuario.getId())
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria informada não existe"));

		boolean eraParcelado = Boolean.TRUE.equals(despesa.getParcelado());
		BigDecimal valorTotalAnterior = despesa.getValor();

		despesa.setNome(request.nome());
		despesa.setDescricao(request.descricao());
		despesa.setCategoria(categoria);

		if (request.dataDespesa() != null) {
			despesa.setDataDespesa(request.dataDespesa());
		}

		despesa.setaPagar(Boolean.TRUE.equals(request.aPagar()));

		if (!Boolean.TRUE.equals(request.parcelado())) {
			despesa.setValor(request.valor());
			despesa.setParcelado(false);
			despesa.setQuantidadeParcela(null);
			despesa.setDataVencimento(request.dataVencimento());

			if (eraParcelado) {
				List<Parcela> parcelas = parcelaRepository.findByDespesaId(despesa.getId());

				if (!parcelas.isEmpty()) {
					parcelaRepository.deleteAll(parcelas);
				}
			}

			despesaRepository.save(despesa);
			return despesaMapper.toDespesaResponse(despesa);
		}

		Integer quantidadeNova = request.quantidadeParcela();

		if (quantidadeNova == null || quantidadeNova <= 0) {
			throw new ControlFinanceException("Informe uma quantidade de parcelas válida");
		}

		despesa.setParcelado(true);
		despesa.setQuantidadeParcela(quantidadeNova);

		List<Parcela> parcelasExistentes = parcelaRepository.findByDespesaId(despesa.getId());

		LocalDate dataPrimeiraParcela = obterDataPrimeiraParcela(parcelasExistentes);

		if (dataPrimeiraParcela == null) {
			dataPrimeiraParcela = request.dataVencimento();
		}

		if (dataPrimeiraParcela == null) {
			throw new ControlFinanceException("Não foi possível determinar a data da primeira parcela");
		}

		despesa.setDataVencimento(dataPrimeiraParcela);

		if (!eraParcelado) {
			despesa.setValor(request.valor());
		} else {
			despesa.setValor(valorTotalAnterior);
		}

		sincronizarParcelas(despesa, parcelasExistentes, quantidadeNova, dataPrimeiraParcela);

		Parcela parcelaEditada = null;

		if (request.numeroParcelaEditada() != null) {
			Integer numeroParcela = request.numeroParcelaEditada();

			if (numeroParcela <= quantidadeNova) {
				parcelaEditada = parcelaRepository.findByDespesaIdAndNumeroParcela(despesa.getId(), numeroParcela)
						.orElseThrow(() -> new ControlFinanceException("Parcela informada não existe"));

				boolean parcelaPaga = Boolean.TRUE.equals(request.parcelaPaga());

				parcelaEditada.setParcelaPaga(parcelaPaga);
				parcelaEditada.setDataPagamento(parcelaPaga ? LocalDate.now() : null);

				parcelaRepository.save(parcelaEditada);
			}
		}

		despesaRepository.save(despesa);

		if (parcelaEditada != null) {
			return despesaMapper.toDespesaResponse(despesa, parcelaEditada);
		}

		return despesaMapper.toDespesaResponse(despesa);
	}

	private LocalDate obterDataPrimeiraParcela(List<Parcela> parcelas) {
		return parcelas.stream().filter(parcela -> parcela.getNumeroParcela() != null)
				.min(Comparator.comparing(Parcela::getNumeroParcela)).map(Parcela::getDataVencimento).orElse(null);
	}

	private void sincronizarParcelas(Despesa despesa, List<Parcela> parcelasExistentes, Integer quantidadeNova,
			LocalDate dataPrimeiraParcela) {

		BigDecimal valorParcela = despesa.getValor().divide(BigDecimal.valueOf(quantidadeNova), 2,
				RoundingMode.HALF_UP);

		List<Parcela> parcelasParaRemover = parcelasExistentes.stream()
				.filter(parcela -> parcela.getNumeroParcela() > quantidadeNova).toList();

		if (!parcelasParaRemover.isEmpty()) {
			parcelaRepository.deleteAll(parcelasParaRemover);
		}

		for (int numero = 1; numero <= quantidadeNova; numero++) {
			Parcela parcela = encontrarParcela(parcelasExistentes, numero);

			LocalDate dataVencimento = dataPrimeiraParcela.plusMonths(numero - 1L);

			if (parcela != null) {
				parcela.setValor(valorParcela);
				parcela.setDataVencimento(dataVencimento);
				parcela.setStatus(true);

				parcelaRepository.save(parcela);
			} else {
				parcela = new Parcela();
				parcela.setNumeroParcela(numero);
				parcela.setValor(valorParcela);
				parcela.setDataVencimento(dataVencimento);
				parcela.setStatus(true);
				parcela.setParcelaPaga(false);
				parcela.setDataPagamento(null);
				parcela.setDespesa(despesa);

				parcelaRepository.save(parcela);
			}
		}

		List<Parcela> parcelasAtuais = parcelaRepository.findByDespesaId(despesa.getId());

		BigDecimal somaParcelas = parcelasAtuais.stream()
				.filter(parcela -> parcela.getNumeroParcela() <= quantidadeNova).map(Parcela::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal diferenca = despesa.getValor().subtract(somaParcelas);

		if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
			Parcela ultimaParcela = parcelasAtuais.stream()
					.filter(parcela -> parcela.getNumeroParcela() <= quantidadeNova)
					.max(Comparator.comparing(Parcela::getNumeroParcela)).orElseThrow();

			ultimaParcela.setValor(ultimaParcela.getValor().add(diferenca));

			parcelaRepository.save(ultimaParcela);
		}
	}

	private Parcela encontrarParcela(List<Parcela> parcelas, Integer numeroParcela) {

		for (Parcela parcela : parcelas) {
			if (parcela.getNumeroParcela().equals(numeroParcela)) {
				return parcela;
			}
		}

		return null;
	}

	public Page<DespesaResponse> findAll(int page, int size, Integer mes) {

		Usuario usuario = getUsuarioAutenticado();

		List<Parcela> parcelas;

		if (mes != null) {
			parcelas = parcelaRepository.buscarPorUsuarioEMes(usuario.getId(), mes);
		} else {
			parcelas = parcelaRepository.buscarPorUsuario(usuario.getId());
		}

		List<Despesa> despesasNaoParceladas = despesaRepository.buscarDespesasNaoParceladas(usuario.getId(), mes);

		List<DespesaResponse> responses = new ArrayList<>();

		for (Despesa despesa : despesasNaoParceladas) {
			responses.add(despesaMapper.toDespesaResponse(despesa));
		}

		for (Parcela parcela : parcelas) {
			responses.add(despesaMapper.toDespesaResponse(parcela.getDespesa(), parcela));
		}

		responses.sort(
				Comparator.comparing(DespesaResponse::dataVencimento, Comparator.nullsLast(Comparator.naturalOrder())));

		int inicio = page * size;
		int fim = Math.min(inicio + size, responses.size());

		List<DespesaResponse> pagina;

		if (inicio >= responses.size()) {
			pagina = new ArrayList<>();
		} else {
			pagina = responses.subList(inicio, fim);
		}

		Pageable pageable = PageRequest.of(page, size);

		return new PageImpl<>(pagina, pageable, responses.size());
	}

	@Transactional
	public DespesaResponse updateStatus(Long id) {
		if (id == null || id <= 0) {
			throw new DespesaNaoEncontradaException("Informe uma despesa válida");
		}

		Usuario usuario = getUsuarioAutenticado();

		Despesa despesa = despesaRepository.findByIdAndStatusTrueAndUsuarioId(id, usuario.getId())
				.orElseThrow(() -> new DespesaNaoEncontradaException("Despesa informada não existe"));

		despesa.setStatus(false);

		List<Parcela> parcelas = parcelaRepository.findByDespesaId(despesa.getId());

		for (Parcela parcela : parcelas) {
			parcela.setStatus(false);
		}

		parcelaRepository.saveAll(parcelas);
		despesaRepository.save(despesa);

		return despesaMapper.toDespesaResponse(despesa);
	}

	public BigDecimal sumDespesas(Integer mes) {
		Usuario usuario = getUsuarioAutenticado();

		return despesaRepository.sumDespesas(usuario.getId(), mes);
	}

	public List<TotalDespesaCategoriaResponse> totalDespesaCategoriaResponse(Integer mes) {

		Usuario usuario = getUsuarioAutenticado();

		return despesaRepository.totalDespesaCategorias(usuario.getId(), mes);
	}

	private Usuario getUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			throw new RuntimeException("Usuário não autenticado");
		}

		String email = authentication.getName();

		if (email == null || email.isBlank()) {
			throw new RuntimeException("Usuário autenticado não possui email");
		}

		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
	}
}