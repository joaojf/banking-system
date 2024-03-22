package com.bank.service;

import com.bank.domain.Conta;
import com.bank.exception.BadRequestException;
import com.bank.repository.ContaRepository;
import com.bank.request.ContaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
@Service
public class ContaService {
    private final ContaRepository repository;
    private static final Random random = new Random();

    public List<Conta> listAllNoPageable() {
        return repository.findAll();
    }

    public Page<Conta> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Conta findByIdOrThrowBadRequestException(long id) {
        return repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found id " + id));
    }

    public Conta save() {
        return repository.save(Conta.builder().identificadorConta(identificadorConta()).saldo(ZERO).build());
    }

    public void update(long id, ContaRequest request) {
        Conta savedConta = findByIdOrThrowBadRequestException(id);
        Conta contaUpdated = Conta.builder()
                .id(savedConta.getId())
                .identificadorConta(request.getIdentificadorConta())
                .saldo(savedConta.getSaldo())
                .createdAt(savedConta.getCreatedAt())
                .build();
        repository.save(contaUpdated);
    }

    public void delete(long id) {
        repository.delete(findByIdOrThrowBadRequestException(id));
    }

    public Conta findByIdentificadorContaOrThrowBadRequestException(String identificador) {
        return repository.findByIdentificadorConta(identificador).orElseThrow(() -> new BadRequestException("Not Found identificador " + identificador));
    }

    private static String identificadorConta() {
        String numero = String.valueOf(random.nextInt(900000) + 100000);
        return numero.substring(0, 5) + "-" + numero.substring(5);
    }
}
