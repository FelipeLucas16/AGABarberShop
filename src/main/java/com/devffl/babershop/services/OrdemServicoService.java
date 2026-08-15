package com.devffl.babershop.services;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.dto.OrdemServicoDtoRelatorio;
import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.dto.RelatorioFinanceiroItemDto;
import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.entities.*;
import com.devffl.babershop.enums.MetodoPagamento;
import com.devffl.babershop.enums.StatusPagamento;
import com.devffl.babershop.repositories.OrdemServicoRepository;
import com.devffl.babershop.repositories.ProdutoRepository;
import com.devffl.babershop.repositories.ServicosRepository;
import com.devffl.babershop.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ServicosRepository servicosRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<OrdemServicoDto> findAll() {
        return ordemServicoRepository.findAll().stream()
                .map(OrdemServico::toDto)
                .toList();
    }

    @Transactional
    public OrdemServicoDto findById(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada."));
        return ordemServico.toDto();
    }

    @Transactional
    public OrdemServicoDto novaOrdemServico(OrdemServicoDto ordemServicoDto) {
        User user = userRepository.findById(ordemServicoDto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + ordemServicoDto.getUser().getId()));

        List<Long> servicoIds = ordemServicoDto.getServicos().stream()
                .map(ServicoDto::getId)
                .collect(Collectors.toList());
        List<Servicos> servicos = servicosRepository.findAllById(servicoIds);

        List<Long> produtoIds = ordemServicoDto.getProdutos().stream()
                .map(ProdutoDto::getId)
                .collect(Collectors.toList());
        List<Produto> produtos = produtoRepository.findAllById(produtoIds);

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setUser(user);
        ordemServico.setServicos(servicos);
        ordemServico.setProdutos(produtos);
        ordemServico.setValorTotal(calcularValorTotal(servicos, produtos));
        ordemServico.setStatus(StatusPagamento.ABERTO);

        OrdemServico ordemSalva = ordemServicoRepository.save(ordemServico);
        return ordemSalva.toDto();
    }

    @Transactional
    public OrdemServicoDto finalizarPagamento(Long id, MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Informe o método de pagamento para finalizar a ordem de serviço.");
        }

        OrdemServico ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada."));

        ordemServico.setStatus(StatusPagamento.PAGO);
        ordemServico.setMetodoPagamento(metodoPagamento);

        OrdemServico ordemAtualizada = ordemServicoRepository.save(ordemServico);
        return ordemAtualizada.toDto();
    }

    private Double calcularValorTotal(List<Servicos> servicos, List<Produto> produtos) {
        double totalServicos = servicos.stream().mapToDouble(Servicos::getPreco).sum();
        double totalProdutos = produtos.stream().mapToDouble(Produto::getPreco).sum();
        return totalServicos + totalProdutos;
    }

    @Transactional
    public void deleteById(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada."));
        ordemServicoRepository.delete(ordemServico);
    }

    @Transactional
    public ResponseEntity<byte[]> gerarRelatorioPdf(LocalDate inicio, LocalDate fim) {
        try {
            List<OrdemServico> ordens = buscarOrdensPorPeriodo(inicio, fim);

            List<OrdemServicoDtoRelatorio> ordensFormatadas = ordens.stream().map(o -> {
                String servicosStr = o.getServicos().stream()
                        .map(s -> s.getNome() + " (R$ " + String.format("%.2f", s.getPreco()) + ")")
                        .collect(Collectors.joining(", "));

                String produtosStr = o.getProdutos().stream()
                        .map(p -> p.getNome() + " (R$ " + String.format("%.2f", p.getPreco()) + ")")
                        .collect(Collectors.joining(", "));

                return new OrdemServicoDtoRelatorio(
                        o.getId(),
                        o.getUser().getNome(),
                        servicosStr,
                        produtosStr,
                        o.getValorTotal(),
                        formatarStatus(o.getStatus()),
                        formatarMetodoPagamento(o.getMetodoPagamento())
                );
            }).collect(Collectors.toList());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ordensFormatadas);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", "Barbearia AGA");
            parametros.put("titulo", "Relatório de Ordens de Serviço");
            parametros.put("periodo", formatarPeriodo(inicio, fim));

            InputStream inputStream = getClass().getResourceAsStream("/ordens_servico.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ordens_servico.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @Transactional
    public ResponseEntity<byte[]> gerarRelatorioFinanceiroPdf(LocalDate inicio, LocalDate fim) {
        try {
            List<OrdemServico> ordens = buscarOrdensPorPeriodo(inicio, fim);

            double faturamentoTotal = ordens.stream()
                    .mapToDouble(o -> o.getValorTotal() != null ? o.getValorTotal() : 0.0)
                    .sum();

            double totalPago = ordens.stream()
                    .filter(o -> o.getStatus() == StatusPagamento.PAGO)
                    .mapToDouble(o -> o.getValorTotal() != null ? o.getValorTotal() : 0.0)
                    .sum();

            double totalEmAberto = faturamentoTotal - totalPago;

            Map<String, RelatorioFinanceiroItemDto> porServico = new LinkedHashMap<>();
            Map<String, RelatorioFinanceiroItemDto> porProduto = new LinkedHashMap<>();
            Map<String, RelatorioFinanceiroItemDto> porMetodoPagamento = new LinkedHashMap<>();

            for (OrdemServico ordem : ordens) {
                for (Servicos servico : ordem.getServicos()) {
                    acumularItem(porServico, "Serviço", servico.getNome(), servico.getPreco());
                }
                for (Produto produto : ordem.getProdutos()) {
                    acumularItem(porProduto, "Produto", produto.getNome(), produto.getPreco());
                }
                if (ordem.getStatus() == StatusPagamento.PAGO) {
                    acumularItem(porMetodoPagamento, "Forma de Pagamento",
                            formatarMetodoPagamento(ordem.getMetodoPagamento()), ordem.getValorTotal());
                }
            }

            List<RelatorioFinanceiroItemDto> itens = new ArrayList<>();
            porServico.values().stream()
                    .sorted(Comparator.comparing(RelatorioFinanceiroItemDto::getValorTotal).reversed())
                    .forEach(itens::add);
            porProduto.values().stream()
                    .sorted(Comparator.comparing(RelatorioFinanceiroItemDto::getValorTotal).reversed())
                    .forEach(itens::add);
            porMetodoPagamento.values().stream()
                    .sorted(Comparator.comparing(RelatorioFinanceiroItemDto::getValorTotal).reversed())
                    .forEach(itens::add);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itens);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", "Barbearia AGA");
            parametros.put("titulo", "Relatório Financeiro");
            parametros.put("periodo", formatarPeriodo(inicio, fim));
            parametros.put("faturamentoTotal", faturamentoTotal);
            parametros.put("totalPago", totalPago);
            parametros.put("totalEmAberto", totalEmAberto);

            InputStream inputStream = getClass().getResourceAsStream("/relatorio_financeiro.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_financeiro.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private List<OrdemServico> buscarOrdensPorPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null && fim == null) {
            return ordemServicoRepository.findAll();
        }
        LocalDateTime inicioDateTime = inicio != null ? inicio.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime fimDateTime = fim != null ? fim.atTime(LocalTime.MAX) : LocalDateTime.MAX;
        return ordemServicoRepository.findByDataCriacaoBetween(inicioDateTime, fimDateTime);
    }

    private void acumularItem(Map<String, RelatorioFinanceiroItemDto> itens, String tipo, String nome, Double preco) {
        double valor = preco != null ? preco : 0.0;
        RelatorioFinanceiroItemDto item = itens.get(nome);
        if (item == null) {
            itens.put(nome, RelatorioFinanceiroItemDto.builder()
                    .tipo(tipo)
                    .nome(nome)
                    .quantidade(1L)
                    .valorTotal(valor)
                    .build());
        } else {
            item.setQuantidade(item.getQuantidade() + 1);
            item.setValorTotal(item.getValorTotal() + valor);
        }
    }

    private String formatarStatus(StatusPagamento status) {
        if (status == StatusPagamento.PAGO) {
            return "Pago";
        }
        return "Em aberto";
    }

    private String formatarMetodoPagamento(MetodoPagamento metodoPagamento) {
        if (metodoPagamento == null) {
            return "-";
        }
        return switch (metodoPagamento) {
            case DINHEIRO -> "Dinheiro";
            case CARTAO_CREDITO -> "Cartão de Crédito";
            case CARTAO_DEBITO -> "Cartão de Débito";
            case PIX -> "Pix";
        };
    }

    private String formatarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null && fim == null) {
            return "Todas as ordens";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String de = inicio != null ? inicio.format(formatter) : "início";
        String ate = fim != null ? fim.format(formatter) : "hoje";
        return "Período: " + de + " a " + ate;
    }

    @Transactional
    public OrdemServicoDto updateOrdemServico(Long id, OrdemServicoDto ordemServicoDto) {
        OrdemServico ordemServico = ordemServicoRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Ordem de serviço não encontrada"));

        User user = userRepository.findById(ordemServicoDto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + ordemServicoDto.getUser().getId()));

        List<Long> servicoIds = ordemServicoDto.getServicos().stream()
                .map(ServicoDto::getId)
                .collect(Collectors.toList());
        List<Servicos> servicos = servicosRepository.findAllById(servicoIds);

        List<Long> produtoIds = ordemServicoDto.getProdutos().stream()
                .map(ProdutoDto::getId)
                .collect(Collectors.toList());
        List<Produto> produtos = produtoRepository.findAllById(produtoIds);

        ordemServico.setUser(user);
        ordemServico.setServicos(servicos);
        ordemServico.setProdutos(produtos);
        ordemServico.setValorTotal(calcularValorTotal(servicos, produtos));

        OrdemServico ordemAtualizada = ordemServicoRepository.save(ordemServico);
        return ordemAtualizada.toDto();
    }
}
