package com.allanbontempo.pagamentos.application.service;

import com.allanbontempo.pagamentos.domain.entities.Usuario;
import com.allanbontempo.pagamentos.exception.NotFoundException;
import com.allanbontempo.pagamentos.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario save(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, Usuario usuario) {

        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado com o ID: " + id);
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }


    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Usuário com o id: %d não encontrado.", id)));
    }

    @Transactional
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado com o ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

}
