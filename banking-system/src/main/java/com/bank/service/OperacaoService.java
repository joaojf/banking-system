package com.bank.service;

import com.bank.domain.Conta;
import com.bank.domain.Operacao;
import com.bank.exception.BadRequestException;
import com.bank.repository.OperacaoRepository;
import com.bank.request.OperacaoRequest;
import com.bank.request.TransferenciaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.bank.enums.OperacaoEnum.DEPOSITO;
import static com.bank.enums.OperacaoEnum.SAQUE;
import static com.bank.enums.OperacaoEnum.TRANSFERENCIA;


@RequiredArgsConstructor
@Service
public class OperacaoService {
    public static final String O_VALOR_DO_SAQUE_DEVE_SER_MAIOR_QUE_ZERO = "O valor do saque deve ser maior que zero";
    public static final String VALOR_SAQUE_EXCEDIDO = "O valor solicitado para saque excede o saldo disponível na conta. Saldo atual: R$ ";
    public static final String O_VALOR_DA_TRANSFERENCIA_DEVE_SER_MAIOR_QUE_ZERO = "O valor da transferência deve ser maior que zero";
    public static final String VALOR_TRANSFERENCIA_EXCEDIDO = "O valor solicitado para transferência excede o saldo disponível na conta de origem. Saldo atual: R$ ";
    private final OperacaoRepository repository;
    private final ContaService service;

    public BigDecimal saldo(String identificador) {
        return service.findByIdentificadorContaOrThrowBadRequestException(identificador).getSaldo();
    }

    public Conta deposito(OperacaoRequest request) {
        Conta conta = service.findByIdentificadorContaOrThrowBadRequestException(request.getIdentificador());

        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero");

        conta.setSaldo(conta.getSaldo().add(request.getValor()));

        repository.save(
                Operacao.builder()
                        .tipo(DEPOSITO)
                        .valor(request.getValor())
                        .conta(conta)
                        .build()
        );

        return conta;
    }

    public Conta saque(OperacaoRequest request) {
        Conta conta = service.findByIdentificadorContaOrThrowBadRequestException(request.getIdentificador());

        validarOperacao(request.getValor(), conta, O_VALOR_DO_SAQUE_DEVE_SER_MAIOR_QUE_ZERO, VALOR_SAQUE_EXCEDIDO);

        conta.setSaldo(conta.getSaldo().subtract(request.getValor()));
        repository.save(
                Operacao.builder()
                        .tipo(SAQUE)
                        .valor(request.getValor())
                        .conta(conta)
                        .build()
        );

        return conta;
    }

    public List<Operacao> extrato(String identificador) {
        Conta conta = service.findByIdentificadorContaOrThrowBadRequestException(identificador);
        return repository.findAllByConta(conta);
    }

    public void transferencia(TransferenciaRequest request) {
        Conta origem = service.findByIdentificadorContaOrThrowBadRequestException(request.getOrigem());
        Conta destino = service.findByIdentificadorContaOrThrowBadRequestException(request.getDestino());

        if (origem.equals(destino))
            throw new BadRequestException("As contas de origem e destino não podem ser iguais");

        validarOperacao(request.getValor(), origem, O_VALOR_DA_TRANSFERENCIA_DEVE_SER_MAIOR_QUE_ZERO, VALOR_TRANSFERENCIA_EXCEDIDO);

        origem.setSaldo(origem.getSaldo().subtract(request.getValor()));
        destino.setSaldo(destino.getSaldo().add(request.getValor()));

        repository.save(
                Operacao.builder()
                        .tipo(TRANSFERENCIA)
                        .valor(request.getValor())
                        .conta(origem)
                        .build()
        );
        repository.save(
                Operacao.builder()
                        .tipo(TRANSFERENCIA)
                        .valor(request.getValor())
                        .conta(destino)
                        .build()
        );
    }

    private static void validarOperacao(BigDecimal valor, Conta conta, String mensagemValorZero, String mensagemSaldoInsuficiente) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(mensagemValorZero);

        if (valor.compareTo(conta.getSaldo()) > 0)
            throw new BadRequestException(mensagemSaldoInsuficiente + conta.getSaldo());
    }
}
