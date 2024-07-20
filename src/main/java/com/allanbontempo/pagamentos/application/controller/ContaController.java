package com.allanbontempo.pagamentos.application.controller;

import com.allanbontempo.pagamentos.application.dto.ContaDto;
import com.allanbontempo.pagamentos.application.dto.PagamentoDto;
import com.allanbontempo.pagamentos.application.service.ContaService;
import com.allanbontempo.pagamentos.domain.entities.Conta;
import com.allanbontempo.pagamentos.exception.NullParameterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/contas")
@Tag(name = "Contas")
public class ContaController {

    @Autowired
    private ContaService contaService;


    @Operation(summary = "Cria uma nova conta",
            description = "Faz a criação de uma nova conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))})
    })

    @PostMapping
    public ResponseEntity<ContaDto> criarConta(@Valid @RequestBody ContaDto contaDto) {

        if (contaDto == null) {
            throw new NullParameterException("Conta não pode ser null");
        }

        Conta novaConta = contaService.save(contaDto.toConta());
        ContaDto dto = new ContaDto(novaConta);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "Atualiza uma conta.",
            description = "A partir de um ID passado no path uma conta será atualizada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))}),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContaDto> atualizarConta(@PathVariable Long id, @RequestBody ContaDto contaDto) {
        Conta contaAtualizada = contaService.update(id, contaDto.toConta());
        ContaDto dto = new ContaDto(contaAtualizada);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Atualiza um pagamento.",
            description = "É necessário passar o ID de uma conta e o valor para ser realizado o pagamento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))})
    })
    @PutMapping("/{id}/pagamento")
    public ResponseEntity<String> pagamento(@PathVariable Long id, @Valid @RequestBody PagamentoDto pagamentoDto) {
        BigDecimal saldo = contaService.pagarConta(id, pagamentoDto.getValor());
        return ResponseEntity.ok("Pagamento registrado com sucesso. Você possui um saldo de: R$" + saldo);
    }


    @Operation(summary = "Obtém todas as contas.", description = "Caso o JSON de paginação venha vazio o valor default é 20 por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping
    public ResponseEntity<Page<ContaDto>> listarContas(Pageable pageable) {
        Page<Conta> contas = contaService.findAll(pageable);
        Page<ContaDto> dtoPage = contas.map(ContaDto::new);
        return ResponseEntity.ok(dtoPage);
    }


    @Operation(summary = "Obtém uma conta através do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))}),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaDto> buscarContaPorId(@PathVariable Long id) {
        Conta conta = contaService.findById(id);
        ContaDto dto = new ContaDto(conta);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Busca o total já pago em contas.",
            description = "Será feito uma busca de todas as contas pagas e feito uma soma do valor total já pago.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))})
    })
    @GetMapping("/total-pago")
    public ResponseEntity<String> getTotalPago(@RequestParam String dataInicio, @RequestParam String dataFim) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        BigDecimal totalPago = contaService.getTotalPago(start, end);
        return ResponseEntity.ok("Total pago em contas: R$" + totalPago);
    }

    @Operation(summary = "Busca todas as contas pendentes.",
            description = "Será feito uma busca de todas as contas pendentes e retornará ao usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaDto>> getContasPendentes(@RequestParam String dataInicio, @RequestParam String dataFim) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        List<ContaDto> contas = contaService.getContasPendentes(start, end);
        return ResponseEntity.ok(contas);
    }

    @Operation(summary = "Deleta uma conta a partir do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca contas por ID do usuário", description = "Retorna todas as contas associadas a um usuário específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ContaDto>> getContasByUsuarioId(@PathVariable Long usuarioId) {
        List<ContaDto> contas = contaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(contas);
    }


}