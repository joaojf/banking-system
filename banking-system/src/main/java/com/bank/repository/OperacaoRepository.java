package com.bank.repository;

import com.bank.domain.Conta;
import com.bank.domain.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
    List<Operacao> findAllByConta(Conta conta);
}
