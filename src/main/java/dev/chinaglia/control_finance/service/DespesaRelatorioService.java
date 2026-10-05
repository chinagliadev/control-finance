package dev.chinaglia.control_finance.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.chinaglia.control_finance.entitdades.Despesa;
import dev.chinaglia.control_finance.entitdades.Parcela;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.exception.DespesaNaoEncontradaException;
import dev.chinaglia.control_finance.exception.UsuarioNaoEncontradoException;
import dev.chinaglia.control_finance.relatorio.DTO.DespesaRelatorioDTO;
import dev.chinaglia.control_finance.repository.DespesaRepository;
import dev.chinaglia.control_finance.repository.ParcelaRepository;
import dev.chinaglia.control_finance.repository.UsuarioRepository;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class DespesaRelatorioService {
	private static final String CAMINHO_RELATORIO = "relatorio/ControleFinanceiroRelatorio.jrxml";
	private final DespesaRepository despesaRepository;
	private final ParcelaRepository parcelaRepository;
	private final UsuarioRepository usuarioRepository;

	public DespesaRelatorioService(DespesaRepository despesaRepository, ParcelaRepository parcelaRepository,
			UsuarioRepository usuarioRepository) {
		this.despesaRepository = despesaRepository;
		this.parcelaRepository = parcelaRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public byte[] gerarRelatorio(Integer mes) throws Exception {
		Usuario usuario = getUsuarioAutenticado();
		Long usuarioId = usuario.getId();
		List<DespesaRelatorioDTO> despesaRelatorio = montarLista(usuarioId, mes);
		BigDecimal total = despesaRepository.sumDespesas(usuarioId, mes);
		Map<String, Object> params = new HashMap<>();
		params.put("titulo", "Controle Financeiro");
		params.put("totalDespesas", total);
		params.put("usuario", usuario.getNome());
		params.put("mesDespesa", obterNomeMes(mes));
		try (InputStream jrxml = new ClassPathResource(CAMINHO_RELATORIO).getInputStream()) {
			JasperReport report = JasperCompileManager.compileReport(jrxml);
			JasperPrint print = JasperFillManager.fillReport(report, params,
					new JRBeanCollectionDataSource(despesaRelatorio));
			return JasperExportManager.exportReportToPdf(print);
		}
	}

	@Transactional(readOnly = true)
	public byte[] gerarExcel(Integer mes) throws Exception {
		Usuario usuario = getUsuarioAutenticado();
		List<DespesaRelatorioDTO> despesas = montarLista(usuario.getId(), mes);
		BigDecimal total = despesaRepository.sumDespesas(usuario.getId(), mes);
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Controle Financeiro");
			Font negrito = workbook.createFont();
			negrito.setBold(true);
			Font titulo = workbook.createFont();
			titulo.setBold(true);
			titulo.setFontHeightInPoints((short) 16);
			CellStyle estiloTitulo = workbook.createCellStyle();
			estiloTitulo.setFont(titulo);
			CellStyle estiloNegrito = workbook.createCellStyle();
			estiloNegrito.setFont(negrito);
			CellStyle estiloHeader = workbook.createCellStyle();
			estiloHeader.setFont(negrito);
			estiloHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			estiloHeader.setBorderBottom(BorderStyle.THIN);
			DataFormat formato = workbook.createDataFormat();
			CellStyle estiloData = workbook.createCellStyle();
			estiloData.setDataFormat(formato.getFormat("dd/MM/yyyy"));
			CellStyle estiloValor = workbook.createCellStyle();
			estiloValor.setDataFormat(formato.getFormat("R$ #,##0.00"));
			CellStyle estiloTotal = workbook.createCellStyle();
			estiloTotal.setFont(negrito);
			estiloTotal.setDataFormat(formato.getFormat("R$ #,##0.00"));
			int linha = 0;
			Cell cTitulo = sheet.createRow(linha++).createCell(0);
			cTitulo.setCellValue("Controle Financeiro");
			cTitulo.setCellStyle(estiloTitulo);
			Row rUsuario = sheet.createRow(linha++);
			rUsuario.createCell(0).setCellValue("Usuário:");
			rUsuario.getCell(0).setCellStyle(estiloNegrito);
			rUsuario.createCell(1).setCellValue(usuario.getNome());
			Row rMes = sheet.createRow(linha++);
			rMes.createCell(0).setCellValue("Mês:");
			rMes.getCell(0).setCellStyle(estiloNegrito);
			rMes.createCell(1).setCellValue(obterNomeMes(mes));
			linha++;
			String[] colunas = { "Despesa", "Categoria", "Parcela", "Data", "Valor", "Situação" };
			Row header = sheet.createRow(linha++);
			for (int i = 0; i < colunas.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(colunas[i]);
				cell.setCellStyle(estiloHeader);
			}
			for (DespesaRelatorioDTO d : despesas) {
				Row row = sheet.createRow(linha++);
				row.createCell(0).setCellValue(d.getNome());
				row.createCell(1).setCellValue(d.getCategoria());
				row.createCell(2).setCellValue(d.getQuantidade_parcelada());
				Cell cData = row.createCell(3);
				if (d.getDataRef() != null) {
					cData.setCellValue(java.sql.Date.valueOf(d.getDataRef()));
				}
				cData.setCellStyle(estiloData);
				Cell cValor = row.createCell(4);
				cValor.setCellValue(d.getValor() != null ? d.getValor().doubleValue() : 0);
				cValor.setCellStyle(estiloValor);
				row.createCell(5).setCellValue(d.getSituacao());
			}
			Row rTotal = sheet.createRow(linha);
			Cell cLabel = rTotal.createCell(3);
			cLabel.setCellValue("Total:");
			cLabel.setCellStyle(estiloNegrito);
			Cell cTotal = rTotal.createCell(4);
			cTotal.setCellValue(total != null ? total.doubleValue() : 0);
			cTotal.setCellStyle(estiloTotal);
			for (int i = 0; i < colunas.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(out);
			return out.toByteArray();
		}
	}

	private List<DespesaRelatorioDTO> montarLista(Long usuarioId, Integer mes) {

	    List<DespesaRelatorioDTO> lista = new ArrayList<>();

	    for (Despesa d : despesaRepository.buscarDespesasNaoParceladas(usuarioId, mes)) {

	        String situacao = definirSituacao(d.getaPagar(), d.getDataVencimento());
	        String categoria = d.getCategoria() != null ? d.getCategoria().getNome() : "";

	        lista.add(montarDto(d.getNome(), categoria, 0, d.getDataDespesa(), d.getValor(), situacao));
	    }

	    List<Parcela> parcelas = parcelaRepository.buscarPorUsuarioEMes(usuarioId, mes);

	    Map<Long, Parcela> despesasParceladas = new HashMap<>();

	    for (Parcela p : parcelas) {
	        Despesa d = p.getDespesa();

	        if (!despesasParceladas.containsKey(d.getId())) {
	            despesasParceladas.put(d.getId(), p);
	        }
	    }

	    for (Parcela p : despesasParceladas.values()) {

	        Despesa d = p.getDespesa();

	        String situacao = definirSituacaoParcela(p);
	        String categoria = d.getCategoria() != null ? d.getCategoria().getNome() : "";
	        Integer qtdParcela = d.getQuantidadeParcela() != null ? d.getQuantidadeParcela() : 0;

	        lista.add(montarDto(d.getNome(), categoria, qtdParcela, p.getDataVencimento(), p.getValor(), situacao));
	    }

	    if (lista.isEmpty()) {
	        throw new DespesaNaoEncontradaException("Não existem despesas para o período selecionado.");
	    }

	    lista.sort(Comparator.comparing(DespesaRelatorioDTO::getDataRef, Comparator.nullsLast(Comparator.naturalOrder())));

	    return lista;
	}

	private String obterNomeMes(Integer mes) {
		if (mes == null) {
			return "Todos os Meses";
		}
		String nome = Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
		return nome.substring(0, 1).toUpperCase() + nome.substring(1);
	}

	private DespesaRelatorioDTO montarDto(String nome, String categoria, Integer quantidadeParcelada, LocalDate data,
			BigDecimal valor, String situacao) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DespesaRelatorioDTO dto = new DespesaRelatorioDTO(nome, categoria, quantidadeParcelada,
				data != null ? data.format(fmt) : "", valor, situacao);
		dto.setDataRef(data);
		return dto;
	}

	private String definirSituacao(Boolean aPagar, LocalDate dataVencimento) {
		if (Boolean.FALSE.equals(aPagar)) {
			return "Paga";
		}
		if (dataVencimento == null) {
			return "Paga";
		}
		return definirSituacaoPorData(dataVencimento);
	}

	private String definirSituacaoParcela(Parcela parcela) {
		if (Boolean.TRUE.equals(parcela.getParcelaPaga())) {
			return "Paga";
		}
		return definirSituacaoPorData(parcela.getDataVencimento());
	}

	private String definirSituacaoPorData(LocalDate dataVencimento) {
		LocalDate hoje = LocalDate.now();
		long dias = hoje.until(dataVencimento).getDays();
		if (dataVencimento.isBefore(hoje)) {
			return "Vencida";
		}
		if (dias <= 5) {
			return "Urgente";
		}
		if (dias < 10) {
			return "Atenção";
		}
		return "No prazo";
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