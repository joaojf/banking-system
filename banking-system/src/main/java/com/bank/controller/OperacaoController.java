package com.bank.controller;

import com.bank.domain.Conta;
import com.bank.domain.Operacao;
import com.bank.request.OperacaoRequest;
import com.bank.request.TransferenciaRequest;
import com.bank.service.OperacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("operacao")
@RestController
public class OperacaoController {
    private final OperacaoService service;

    @GetMapping("/{identificador}")
    public ResponseEntity<List<Operacao>> extrato(@PathVariable String identificador) {
        return ResponseEntity.ok(service.extrato(identificador));
    }

    @GetMapping("/consulta-saldo/{identificador}")
    public ResponseEntity<BigDecimal> saldo(@PathVariable String identificador) {
        return ResponseEntity.ok(service.saldo(identificador));
    }

    @PostMapping("/deposito")
    public ResponseEntity<Conta> deposito(@RequestBody OperacaoRequest request) {
        return ResponseEntity.ok(service.deposito(request));
    }

    @PostMapping("/saque")
    public ResponseEntity<Conta> saque(@RequestBody OperacaoRequest request) {
        return ResponseEntity.ok(service.saque(request));
    }

    @PostMapping("/transferencia")
    public ResponseEntity<Void> transferencia(@RequestBody TransferenciaRequest request) {
        service.transferencia(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
