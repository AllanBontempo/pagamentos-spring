package com.allanbontempo.pagamentos.application.dto;

import com.allanbontempo.pagamentos.domain.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private BigDecimal saldo;

    public UsuarioDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.saldo = usuario.getSaldo();
    }

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();

        usuario.setId(this.id);
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        usuario.setSenha(this.senha);
        usuario.setSaldo(this.saldo);

        return usuario;
    }
}
