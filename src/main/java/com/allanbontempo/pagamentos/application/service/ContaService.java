package com.allanbontempo.pagamentos.application.service;

import com.allanbontempo.pagamentos.application.dto.ContaDto;
import com.allanbontempo.pagamentos.domain.entities.Conta;
import com.allanbontempo.pagamentos.domain.entities.Usuario;
import com.allanbontempo.pagamentos.domain.enums.Situacao;
import com.allanbontempo.pagamentos.exception.NotFoundException;
import com.allanbontempo.pagamentos.exception.ValorPagoInvalidoException;
import com.allanbontempo.pagamentos.infrastructure.repository.ContaRepository;
import com.allanbontempo.pagamentos.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Conta save(Conta conta) {

        if (conta.getSituacao() == null) {
            conta.setSituacao(Situacao.PENDENTE);
        }

        Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        conta.setUsuario(usuario);

        return contaRepository.save(conta);
    }

    @Transactional
    public Conta update(Long id, Conta conta) {

        Conta contaAntiga = contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        if (conta.getSituacao() == null) {
            conta.setSituacao(contaAntiga.getSituacao());
        }

        conta.setId(id);
        conta.setUsuario(usuario);

        return contaRepository.save(conta);
    }

    @Transactional
    public BigDecimal pagarConta(Long id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));

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
        return contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
    }

    public BigDecimal getTotalPago(LocalDate dataInicio, LocalDate dataFim) {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getDataPagamento() != null)
                .filter(conta -> conta.getSituacao() == Situacao.PAGO)
                .filter(conta -> !conta.getDataPagamento().isBefore(dataInicio) && !conta.getDataPagamento().isAfter(dataFim))
                .map(Conta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ContaDto> getContasPendentes(LocalDate dataInicio, LocalDate dataFim) {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getDataPagamento() == null)
                .filter(conta -> conta.getSituacao() == Situacao.PENDENTE)
                .filter(conta -> !conta.getDataVencimento().isBefore(dataInicio) && !conta.getDataVencimento().isAfter(dataFim))
                .map(ContaDto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void delete(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new NotFoundException("Conta não encontrada com ID: " + id);
        }
        contaRepository.deleteById(id);
    }

    public List<ContaDto> findByUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Conta> contas = contaRepository.findByUsuario(usuario);
        return contas.stream().map(ContaDto::new).collect(Collectors.toList());
    }
}
