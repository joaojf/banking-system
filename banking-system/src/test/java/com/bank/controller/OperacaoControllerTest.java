package com.bank.controller;

import com.bank.domain.Conta;
import com.bank.domain.Operacao;
import com.bank.enums.OperacaoEnum;
import com.bank.request.OperacaoRequest;
import com.bank.request.TransferenciaRequest;
import com.bank.service.OperacaoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
class OperacaoControllerTest {
    public static final String IDENTIFICADOR_CONTA = "12345-6";
    @Mock
    private OperacaoService operacaoService;
    @InjectMocks
    OperacaoController operacaoController;

    @BeforeEach
    void setUp() {
        Mockito.when(operacaoService.extrato(ArgumentMatchers.anyString())).thenReturn(List.of(createOperacao()));

        Mockito.when(operacaoService.saldo(ArgumentMatchers.anyString())).thenReturn(createConta().getSaldo());

        Mockito.when(operacaoService.deposito(ArgumentMatchers.any(OperacaoRequest.class))).thenReturn(createContaDeposito());

        Mockito.when(operacaoService.saque(ArgumentMatchers.any(OperacaoRequest.class))).thenReturn(createContaSaque());

        Mockito.doNothing().when(operacaoService).transferencia(ArgumentMatchers.any(TransferenciaRequest.class));
    }

    @Test
    @DisplayName("extrato should return list of operacao when successful")
    void extratoShouldReturnListOperacaoWhenSuccessful() {
        List<Operacao> operacaoList = operacaoController.extrato(IDENTIFICADOR_CONTA).getBody();

        Assertions.assertThat(operacaoList).isNotEmpty().isNotNull().hasSize(1);
        Mockito.verify(operacaoService, Mockito.times(1)).extrato(IDENTIFICADOR_CONTA);
    }

    @Test
    @DisplayName("saldo should return saldo when successful")
    void saldoShouldReturnSaldoWhenSuccessful() {
        BigDecimal saldo = operacaoController.saldo(IDENTIFICADOR_CONTA).getBody();
        Assertions.assertThat(saldo).isNotNull().isEqualTo(createConta().getSaldo());
        Mockito.verify(operacaoService, Mockito.times(1)).saldo(IDENTIFICADOR_CONTA);
    }

    @Test
    @DisplayName("deposito should return conta with saldo added when successful")
    void depositoShouldReturnContaWithSaldoAddedWhenSuccessful() {
        Conta saldoAntesDeposito = createConta();
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(IDENTIFICADOR_CONTA).valor(BigDecimal.TEN).build();

        Conta contaDepoisDeposito = operacaoController.deposito(operacaoRequest).getBody();

        Mockito.verify(operacaoService, Mockito.times(1)).deposito(operacaoRequest);
        Assertions.assertThat(contaDepoisDeposito).isNotNull();

        assertNotEquals(contaDepoisDeposito.getSaldo(), createConta().getSaldo(),
                "O saldo da conta após o depósito não deve ser igual ao saldo antes do depósito");

        assertEquals(saldoAntesDeposito.getSaldo().add(BigDecimal.TEN), contaDepoisDeposito.getSaldo(),
                "O saldo após o depósito deve ser igual ao saldo antes do depósito mais o valor do depósito");

        assertTrue(contaDepoisDeposito.getSaldo().compareTo(saldoAntesDeposito.getSaldo()) > 0,
                "O saldo após o depósito deve ser maior do que o saldo antes do depósito");
    }

    @Test
    @DisplayName("saque should return conta with saldo lower when successful")
    void saqueShouldReturnContaWithSadoLowerWhenSuccessful() {
        Conta contaAntesSaque = createConta();
        contaAntesSaque.setSaldo(contaAntesSaque.getSaldo().add(BigDecimal.TEN));

        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(IDENTIFICADOR_CONTA).valor(BigDecimal.valueOf(9)).build();

        Conta contaDepoisSaque = operacaoController.saque(operacaoRequest).getBody();

        Mockito.verify(operacaoService, Mockito.times(1)).saque(operacaoRequest);
        Assertions.assertThat(contaDepoisSaque).isNotNull();

        assertNotEquals(contaDepoisSaque.getSaldo(), contaAntesSaque.getSaldo(),
                "O saldo da conta após o saque não deve ser igual ao saldo antes do saque");

        assertEquals(contaAntesSaque.getSaldo().subtract(BigDecimal.valueOf(9)), contaDepoisSaque.getSaldo(),
                "O saldo após o saque deve ser igual ao saldo antes do saque menos o valor do saque");

        assertTrue(contaDepoisSaque.getSaldo().compareTo(contaAntesSaque.getSaldo()) < 0,
                "O saldo após o saque deve ser menor do que o saldo antes do saque");
    }

    @Test
    @DisplayName("transferencia should update saldos correctly when successful")
    void transferenciaShouldUpdateSaldosCorrectlyWhenSuccessful() {
        TransferenciaRequest transferenciaRequest = TransferenciaRequest.builder().origem("12345-6").destino("12345-7").valor(BigDecimal.TEN).build();

        Assertions.assertThatCode(() -> operacaoController.transferencia(transferenciaRequest).getBody()).doesNotThrowAnyException();

        ResponseEntity<Void> transferencia = operacaoController.transferencia(transferenciaRequest);

        Assertions.assertThat(transferencia.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private static Conta createConta() {
        return Conta.builder().id(1L).identificadorConta(IDENTIFICADOR_CONTA).saldo(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static Conta createContaDeposito() {
        return Conta.builder().id(1L).identificadorConta(IDENTIFICADOR_CONTA).saldo(BigDecimal.TEN)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static Conta createContaSaque() {
        return Conta.builder().id(1L).identificadorConta(IDENTIFICADOR_CONTA).saldo(BigDecimal.ONE)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static Operacao createOperacao() {
        return Operacao.builder().id(1L).tipo(OperacaoEnum.EXTRATO).valor(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).conta(createConta()).build();
    }
}
