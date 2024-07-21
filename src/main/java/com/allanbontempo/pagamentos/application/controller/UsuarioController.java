package com.allanbontempo.pagamentos.application.controller;

import com.allanbontempo.pagamentos.application.dto.LoginRequest;
import com.allanbontempo.pagamentos.application.dto.UsuarioDto;
import com.allanbontempo.pagamentos.application.service.UsuarioService;
import com.allanbontempo.pagamentos.domain.entities.Usuario;
import com.allanbontempo.pagamentos.exception.NullParameterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários")
@Slf4j
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Tentando autenticação");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Autenticação bem-sucedida para o usuário: {}", loginRequest.getEmail());

            // Retorne um token JWT ou outra resposta apropriada
            return "Autenticado com sucesso!";
        } catch (AuthenticationException e) {
            log.error("Erro de autenticação: {}", e.getMessage());

            throw new RuntimeException("Credenciais inválidas");
        }
    }

    @Operation(summary = "Cria um novo usuário",
            description = "Faz a criação de um novo usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))})
    })

    @PostMapping
    public ResponseEntity<UsuarioDto> criarUsuario(@Valid @RequestBody UsuarioDto usuarioDto) {

        if (usuarioDto == null) {
            throw new NullParameterException("Usuário não pode ser null");
        }

        Usuario novoUsuario = usuarioService.save(usuarioDto.toUsuario());
        UsuarioDto dto = new UsuarioDto(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "Atualiza um usuário.",
            description = "A partir de um ID passado no path um usuário será atualizado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDto usuarioDto) {
        Usuario usuarioAtualizado = usuarioService.update(id, usuarioDto.toUsuario());
        UsuarioDto dto = new UsuarioDto(usuarioAtualizado);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Obtém todas os usuários.", description = "Caso o JSON de paginação venha vazio o valor default é 20 por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = UsuarioDto.class))})
    })
    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> listarUsuarios(Pageable pageable) {
        Page<Usuario> usuario = usuarioService.findAll(pageable);
        Page<UsuarioDto> dtoPage = usuario.map(UsuarioDto::new);
        return ResponseEntity.ok(dtoPage);
    }


    @Operation(summary = "Obtém um usuário através do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioDto dto = new UsuarioDto(usuario);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Deleta um usuário a partir do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrada",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
