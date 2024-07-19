package com.allanbontempo.pagamentos.domain.entities;

import com.allanbontempo.pagamentos.domain.enums.Situacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "valor_original")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    private String nome;
    private String descricao;
    private String observacao;


}
