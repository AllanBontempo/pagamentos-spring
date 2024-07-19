package com.allanbontempo.pagamentos.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDto {

    @NotNull(message = "O valor a ser pago não pode ser null")
    private BigDecimal valor;
}