package dev.chinaglia.control_finance.service;

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

    public DespesaRelatorioService(DespesaRepository despesaRepository, ParcelaRepository parcelaRepository, UsuarioRepository usuarioRepository) {
        this.despesaRepository = despesaRepository;
        this.parcelaRepository = parcelaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorio(Integer mes) throws Exception {

        Usuario usuario = getUsuarioAutenticado();

        Long usuarioId = usuario.getId();

        List<DespesaRelatorioDTO> despesaRelatorio = new ArrayList<>();

        List<Despesa> despesas = despesaRepository.buscarDespesasNaoParceladas(usuarioId, mes);

        for (Despesa d : despesas) {

            String situacao = definirSituacao(d.getaPagar(), d.getDataVencimento());

            String categoria = "";

            if (d.getCategoria() != null) {
                categoria = d.getCategoria().getNome();
            }

            DespesaRelatorioDTO dto = montarDto(d.getNome(), categoria, 0, d.getDataDespesa(), d.getValor(), situacao);

            despesaRelatorio.add(dto);
        }

        List<Parcela> parcelas = parcelaRepository.buscarPorUsuarioEMes(usuarioId, mes);

        for (Parcela p : parcelas) {

            Despesa d = p.getDespesa();

            String situacao = definirSituacaoParcela(p);

            String categoria = "";

            if (d.getCategoria() != null) {
                categoria = d.getCategoria().getNome();
            }

            Integer quantidadeParcela = 0;

            if (d.getQuantidadeParcela() != null) {
                quantidadeParcela = d.getQuantidadeParcela();
            }

            DespesaRelatorioDTO dto = montarDto(d.getNome(), categoria, quantidadeParcela, p.getDataVencimento(), p.getValor(), situacao);

            despesaRelatorio.add(dto);
        }

        if (despesaRelatorio.isEmpty()) {
            throw new DespesaNaoEncontradaException("Não existem despesas para o período selecionado.");
        }

        despesaRelatorio.sort(Comparator.comparing(DespesaRelatorioDTO::getDataRef, Comparator.nullsLast(Comparator.naturalOrder())));

        BigDecimal total = despesaRepository.sumDespesas(usuarioId, mes);

        Map<String, Object> params = new HashMap<>();

        params.put("titulo", "Controle Financeiro");
        params.put("totalDespesas", total);
        params.put("usuario", usuario.getNome());

        Locale ptBr = new Locale("pt", "BR");
        
        String nomeMes = null;
        
        if(mes != null) 
        {
            Month mesNumero = Month.of(mes);
            nomeMes = mesNumero.getDisplayName(TextStyle.FULL, ptBr);
            nomeMes = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);
        }
        else 
        {
        	nomeMes="Todos os Meses";
        }
        
        params.put("mesDespesa", nomeMes);
        
        try (InputStream jrxml = new ClassPathResource(CAMINHO_RELATORIO).getInputStream()) {

            JasperReport report = JasperCompileManager.compileReport(jrxml);

            JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(despesaRelatorio));

            return JasperExportManager.exportReportToPdf(print);
        }
    }
    private DespesaRelatorioDTO montarDto(String nome, String categoria, Integer quantidadeParcelada, LocalDate data, BigDecimal valor, String situacao) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DespesaRelatorioDTO dto = new DespesaRelatorioDTO(nome, categoria, quantidadeParcelada, data != null ? data.format(fmt) : "", valor, situacao);
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

        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));
    }
}