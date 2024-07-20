package com.allanbontempo.pagamentos.infrastructure.repository;

import com.allanbontempo.pagamentos.domain.entities.Conta;
import com.allanbontempo.pagamentos.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByUsuario(Usuario usuario);
}
