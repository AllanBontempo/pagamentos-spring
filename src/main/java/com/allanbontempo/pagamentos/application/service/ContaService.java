package com.allanbontempo.pagamentos.application.service;

import com.allanbontempo.pagamentos.domain.entities.Conta;
import com.allanbontempo.pagamentos.domain.enums.Situacao;
import com.allanbontempo.pagamentos.exception.ContaNotFoundException;
import com.allanbontempo.pagamentos.exception.ValorPagoInvalidoException;
import com.allanbontempo.pagamentos.infrastructure.repository.ContaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Transactional
    public Conta save(Conta conta) {
        if (conta.getSituacao() == null) {
            conta.setSituacao(Situacao.PENDENTE);
        }

        return contaRepository.save(conta);
    }

    @Transactional
    public Conta update(Long id, Conta conta) {

        Conta contaAntiga = contaRepository.findById(id).orElseThrow(() -> new ContaNotFoundException("Conta não encontrada"));

        if (conta.getSituacao() == null) {
            conta.setSituacao(contaAntiga.getSituacao());
        }

        conta.setId(id);
        return contaRepository.save(conta);
    }

    @Transactional
    public BigDecimal pagarConta(Long id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new ContaNotFoundException("Conta não encontrada"));

        if (conta.getValor().compareTo(valor) > 0) {
            throw new ValorPagoInvalidoException("O valor pago é menor que o valor da conta");
        }

        LocalDate hoje = LocalDate.now();

        conta.setDataPagamento(hoje);
        conta.setSituacao(Situacao.PAGO);

        contaRepository.save(conta);

        return valor.subtract(conta.getValor());
    }

    public Page<Conta> findAll(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public Conta findById(Long id) {
        return contaRepository.findById(id).orElseThrow(() -> new ContaNotFoundException("Conta não encontrada"));
    }

    public BigDecimal getTotalPago(LocalDate startDate, LocalDate endDate) {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getDataPagamento() != null)
                .filter(conta -> conta.getSituacao() == Situacao.PAGO)
                .filter(conta -> !conta.getDataPagamento().isBefore(startDate) && !conta.getDataPagamento().isAfter(endDate))
                .map(Conta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Transactional
    public void delete(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new ContaNotFoundException("Conta não encontrada com ID: " + id);
        }
        contaRepository.deleteById(id);
    }
}
