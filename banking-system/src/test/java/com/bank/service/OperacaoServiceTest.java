package com.bank.service;

import com.bank.domain.Conta;
import com.bank.domain.Operacao;
import com.bank.enums.OperacaoEnum;
import com.bank.exception.BadRequestException;
import com.bank.repository.OperacaoRepository;
import com.bank.request.OperacaoRequest;
import com.bank.request.TransferenciaRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OperacaoServiceTest {
    public static final String EXISTING_IDENTIFICADOR = "12345-6";
    private static final String NON_EXISTING_IDENTIFICADOR = "12345-6";
    @Mock
    private OperacaoRepository repository;
    @Mock
    private ContaService service;
    @InjectMocks
    private OperacaoService operacaoService;

    @Test
    @DisplayName("saldo should return saldo when successful")
    void saldoShouldReturnSaldoWhenSuccessful() {
        Conta expectedConta = createConta();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR))
                .thenReturn(expectedConta);

        BigDecimal actualSaldo = operacaoService.saldo(EXISTING_IDENTIFICADOR);

        Mockito.verify(service, Mockito.times(1)).findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR);
        Mockito.verifyNoMoreInteractions(service);
        Assertions.assertNotNull(actualSaldo, "O saldo retornado não deveria ser nulo");
        Assertions.assertEquals(expectedConta.getSaldo(), actualSaldo, "O saldo retornado deveria ser igual ao saldo esperado");
    }

    @Test
    @DisplayName("saldo should throw BadRequestException when identificador is not found")
    void saldoShouldThrowBadRequestExceptionWhenIdentificadorIsNotFound() {
        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException((NON_EXISTING_IDENTIFICADOR)))
                .thenThrow(BadRequestException.class);

        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.saldo(NON_EXISTING_IDENTIFICADOR), "Deve lançar BadRequestException quando o identificador não é encontrado");
    }

    @Test
    @DisplayName("deposito should throw BadRequestException when identificador is not found")
    void depositoShouldThrowBadRequestExceptionWhenIdentificadorIsNotFound() {
        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(NON_EXISTING_IDENTIFICADOR)).thenThrow(BadRequestException.class);

        OperacaoRequest operacaoRequestNonIdentificador = OperacaoRequest.builder().identificador(NON_EXISTING_IDENTIFICADOR).valor(BigDecimal.ZERO).build();
        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.deposito(operacaoRequestNonIdentificador), "Deve lançar BadRequestException quando o identificador não é encontrado");
    }
    @Test
    @DisplayName("deposito should return conta with saldo greater than zero when successful")
    void depositoShouldReturnContaWithSaldoGreaterThanZeroWheSuccessful(){
        Conta conta = createConta();
        BigDecimal valorDeposito = BigDecimal.valueOf(10.0);
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorDeposito).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Conta actualSaldo = operacaoService.deposito(operacaoRequest);

        Assertions.assertEquals(valorDeposito, actualSaldo.getSaldo(), "O saldo da conta após o depósito deve ser igual ao saldo anterior mais o valor do depósito");
    }

    @Test
    @DisplayName("deposito should throw IllegalArgumentException when deposito value is zero")
    void depositoShouldThrowIllegalArgumentExceptionWhenDepositoValueIsZero(){
        Conta conta = createConta();
        BigDecimal valorDeposito = BigDecimal.ZERO;
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorDeposito).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Assertions.assertThrows(IllegalArgumentException.class, () -> operacaoService.deposito(operacaoRequest), "O valor do depósito deve ser maior que zero");
    }

    @Test
    @DisplayName("deposito should throw IllegalArgumentException when deposito value less than zero")
    void depositoShouldThrowIllegalArgumentExceptionWhenDepositoValueIsLessThanZero(){
        Conta conta = createConta();
        BigDecimal valorDeposito = BigDecimal.valueOf(-1.0);
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorDeposito).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Assertions.assertThrows(IllegalArgumentException.class, () -> operacaoService.deposito(operacaoRequest), "O valor do depósito deve ser maior que zero");
    }

    @Test
    @DisplayName("saque should throw IllegalArgumentException when saque value less than zero")
    void saqueShouldThrowIllegalArgumentExceptionWhenSaqueValueIsLessThanZero() {
        Conta conta = createConta();
        BigDecimal valorSaque = BigDecimal.valueOf(-1.0);
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorSaque).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Assertions.assertThrows(IllegalArgumentException.class, () -> operacaoService.saque(operacaoRequest), "O valor do depósito deve ser maior que zero");
    }
    @Test
    @DisplayName("saque should throw IllegalArgumentException when saque value is zero")
    void saqueShouldThrowIllegalArgumentExceptionWhenSaqueValueIsZero(){
        Conta conta = createConta();
        BigDecimal valorSaque = BigDecimal.ZERO;
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorSaque).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Assertions.assertThrows(IllegalArgumentException.class, () -> operacaoService.saque(operacaoRequest), "O valor do depósito deve ser maior que zero");
    }

    @Test
    @DisplayName("saque should throw BadRequestException when identificador is not found")
    void saqueShouldThrowBadRequestExceptionWhenIdentificadorIsNotFound() {
        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(NON_EXISTING_IDENTIFICADOR)).thenThrow(BadRequestException.class);

        OperacaoRequest operacaoRequestNonIdentificador = OperacaoRequest.builder().identificador(NON_EXISTING_IDENTIFICADOR).valor(BigDecimal.ZERO).build();
        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.saque(operacaoRequestNonIdentificador), "Deve lançar BadRequestException quando o identificador não é encontrado");
    }
    @Test
    @DisplayName("saque should return an conta with a lower saldo when successful")
    void saqueShouldReturnContaWithLowerSaldoWheSuccessful(){
        Conta conta = createConta();
        conta.setSaldo(conta.getSaldo().add(BigDecimal.valueOf(10.0)));
        BigDecimal valorSaque = BigDecimal.valueOf(5.0);
        OperacaoRequest operacaoRequest = OperacaoRequest.builder().identificador(EXISTING_IDENTIFICADOR).valor(valorSaque).build();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(EXISTING_IDENTIFICADOR)).thenReturn(conta);

        Conta actualSaldo = operacaoService.saque(operacaoRequest);

        Assertions.assertEquals(valorSaque, actualSaldo.getSaldo(), "O saldo da conta após o depósito deve ser igual ao saldo anterior mais o valor do depósito");
    }

    @Test
    @DisplayName("extrato should return operations all when successful")
    void extratoShouldReturnOperationsAllWhenSuccessful() {

        String identificadorConta = "12345-6";
        Conta conta = createConta();
        List<Operacao> operacoesEsperadas = createOperacoes();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(identificadorConta))
                .thenReturn(conta);

        Mockito.when(repository.findAllByConta(conta))
                .thenReturn(operacoesEsperadas);

        List<Operacao> operacoesRetornadas = operacaoService.extrato(identificadorConta);

        Mockito.verify(service, Mockito.times(1))
                .findByIdentificadorContaOrThrowBadRequestException(identificadorConta);

        Mockito.verify(repository, Mockito.times(1))
                .findAllByConta(conta);

        Assertions.assertEquals(operacoesEsperadas, operacoesRetornadas);

    }

    @Test
    @DisplayName("extrato should throw BadRequestException when identificador is not found")
    void extratoShouldThrowBadRequestExceptionWhenIdentificadorIsNotFound() {
        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(NON_EXISTING_IDENTIFICADOR))
                .thenThrow(BadRequestException.class);

        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.extrato(NON_EXISTING_IDENTIFICADOR), "Deve lançar BadRequestException quando o identificador não é encontrado");
    }

    @Test
    @DisplayName("transferencia should update saldos correctly when successful")
    void transferenciaShouldUpdateSaldosCorrectlyWhenSuccessful() {
        Conta origemConta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.valueOf(100.0)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Conta destinoConta = Conta.builder().id(2L).identificadorConta("12345-7").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        BigDecimal valor = BigDecimal.valueOf(10.0);

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(origemConta.getIdentificadorConta())).thenReturn(origemConta);
        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(destinoConta.getIdentificadorConta())).thenReturn(destinoConta);

        TransferenciaRequest transferenciaRequest = TransferenciaRequest.builder()
                .origem(origemConta.getIdentificadorConta()).destino(destinoConta.getIdentificadorConta()).valor(valor).build();

        BigDecimal saldoOrigemEsperado = origemConta.getSaldo().subtract(valor);
        BigDecimal saldoDestinoEsperado = destinoConta.getSaldo().add(valor);
        operacaoService.transferencia(transferenciaRequest);

        Assertions.assertEquals(saldoOrigemEsperado, origemConta.getSaldo(), "O saldo da conta de origem após a transferência deve ser igual ao saldo anterior menos o valor transferido");
        Assertions.assertEquals(saldoDestinoEsperado, destinoConta.getSaldo(), "O saldo da conta de destino após a transferência deve ser igual ao saldo anterior mais o valor transferido");
    }
    @Test
    @DisplayName("transferencia should throw BadRequestException when contas are equal")
    void transferenciaShouldThrowBadRequestExceptionWhenAccountsAreEqual() {
        Conta conta = createConta();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(conta.getIdentificadorConta()))
                .thenReturn(conta);

        TransferenciaRequest transferenciaRequest = TransferenciaRequest.builder()
                .origem(conta.getIdentificadorConta())
                .destino(conta.getIdentificadorConta())
                .valor(BigDecimal.valueOf(10.0))
                .build();

        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.transferencia(transferenciaRequest),
                "Transferência com contas iguais deve lançar BadRequestException");
    }
    @Test
    @DisplayName("transferencia should throw BadRequestException when valor transfererecia exceeds saldo")
    void transferenciaShouldThrowBadRequestExceptionWhenTransferAmountExceedsBalance() {
        Conta conta = createConta();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(conta.getIdentificadorConta()))
                .thenReturn(conta);

        TransferenciaRequest transferenciaRequest = TransferenciaRequest.builder()
                .origem(conta.getIdentificadorConta())
                .destino("12345-7")
                .valor(BigDecimal.valueOf(1.0))
                .build();

        Assertions.assertThrows(BadRequestException.class, () -> operacaoService.transferencia(transferenciaRequest));
    }

    @Test
    @DisplayName("transferencia should throw IllegalArgumentException when valor transferencia is zero or negative")
    void transferenciaShouldThrowIllegalArgumentExceptionWhenTransferAmountIsZeroOrNegative() {
        Conta conta = createConta();

        Mockito.when(service.findByIdentificadorContaOrThrowBadRequestException(conta.getIdentificadorConta()))
                .thenReturn(conta);

        TransferenciaRequest transferenciaRequest = TransferenciaRequest.builder()
                .origem(conta.getIdentificadorConta())
                .destino("12345-7")
                .valor(BigDecimal.ZERO)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> operacaoService.transferencia(transferenciaRequest));
    }

    private static List<Operacao> createOperacoes() {

        List<Operacao> operacoes = new ArrayList<>();

        operacoes.add(
                Operacao.builder().id(1L).tipo(OperacaoEnum.DEPOSITO).valor(BigDecimal.valueOf(10.0)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(createConta()).build()
        );
        operacoes.add(
                Operacao.builder().id(1L).tipo(OperacaoEnum.SAQUE).valor(BigDecimal.valueOf(3.0)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(createConta()).build()
        );
        return operacoes;
    }

    private static Conta createConta() {
        return Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }
}
