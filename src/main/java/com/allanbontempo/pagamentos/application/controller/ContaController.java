package com.allanbontempo.pagamentos.application.controller;

import com.allanbontempo.pagamentos.application.dto.ContaDto;
import com.allanbontempo.pagamentos.application.dto.PagamentoDto;
import com.allanbontempo.pagamentos.application.service.ContaService;
import com.allanbontempo.pagamentos.domain.entities.Conta;
import com.allanbontempo.pagamentos.exception.NullParameterException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaDto> criarConta(@Valid @RequestBody ContaDto contaDto) {

        if (contaDto == null) {
            throw new NullParameterException("Conta não pode ser null");
        }

        Conta novaConta = contaService.save(contaDto.toConta());
        ContaDto dto = new ContaDto(novaConta);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaDto> atualizarConta(@PathVariable Long id, @RequestBody ContaDto contaDto) {
        Conta contaAtualizada = contaService.update(id, contaDto.toConta());
        ContaDto dto = new ContaDto(contaAtualizada);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/pagamento")
    public ResponseEntity<String> pagamento(@PathVariable Long id, @Valid @RequestBody PagamentoDto pagamentoDto) {
        BigDecimal saldo = contaService.pagarConta(id, pagamentoDto.getValor());
        return ResponseEntity.ok("Pagamento registrado com sucesso. Você possui um saldo de: R$" + saldo);
    }

    @GetMapping
    public ResponseEntity<Page<ContaDto>> listarContas(Pageable pageable) {
        Page<Conta> contas = contaService.findAll(pageable);
        Page<ContaDto> dtoPage = contas.map(ContaDto::new);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDto> buscarContaPorId(@PathVariable Long id) {
        Conta conta = contaService.findById(id);
        ContaDto dto = new ContaDto(conta);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/total-pago")
    public ResponseEntity<String> getTotalPago(@RequestParam String dataInicio, @RequestParam String dataFim) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        BigDecimal totalPago = contaService.getTotalPago(start, end);
        return ResponseEntity.ok("Total pago em contas: R$" + totalPago);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaDto>> getContasPendentes(@RequestParam String dataInicio, @RequestParam String dataFim) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        List<ContaDto> contas = contaService.getContasPendentes(start, end);
        return ResponseEntity.ok(contas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}