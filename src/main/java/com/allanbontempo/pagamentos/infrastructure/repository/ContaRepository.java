package com.allanbontempo.pagamentos.infrastructure.repository;

import com.allanbontempo.pagamentos.domain.entities.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
}
