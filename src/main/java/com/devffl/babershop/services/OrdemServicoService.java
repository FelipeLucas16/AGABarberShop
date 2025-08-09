package com.devffl.babershop.services;

import com.devffl.babershop.dto.OrdemServicoDto;
import com.devffl.babershop.dto.OrdemServicoDtoRelatorio;
import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.dto.ServicoDto;
import com.devffl.babershop.entities.*;
import com.devffl.babershop.repositories.OrdemServicoRepository;
import com.devffl.babershop.repositories.ProdutoRepository;
import com.devffl.babershop.repositories.ServicosRepository;
import com.devffl.babershop.repositories.UserRepository;
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
import java.util.HashMap;
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

        OrdemServico ordemSalva = ordemServicoRepository.save(ordemServico);
        return ordemSalva.toDto();
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
    public ResponseEntity<byte[]> gerarRelatorioPdf() {
        try {
            List<OrdemServicoDto> ordens = findAll();

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
                        o.getValorTotal()
                );
            }).collect(Collectors.toList());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ordensFormatadas);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("empresa", "Barbearia AGA");
            parametros.put("titulo", "Relatório de Ordens de Serviço");

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


}
