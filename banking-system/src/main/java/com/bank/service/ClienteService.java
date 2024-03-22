package com.bank.service;

import com.bank.domain.Cliente;
import com.bank.domain.Conta;
import com.bank.exception.BadRequestException;
import com.bank.repository.ClienteRepository;
import com.bank.request.ClienteRequest;
import com.bank.request.ClienteRequestUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final ContaService contaService;

    public List<Cliente> listAllNoPageable() {
        return repository.findAll();
    }

    public Conta getAccountById(long id) {
        return contaService.findByIdOrThrowBadRequestException(id);
    }

    public Cliente findByIdOrThrowBadRequestException(long id) {
        return repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found id " + id));
    }

    @Transactional
    public Cliente save(ClienteRequest clienteRequest) {
        Cliente cliente = Cliente.builder()
                .nome(clienteRequest.getNome())
                .documento(clienteRequest.getDocumento())
                .conta(contaService.findByIdOrThrowBadRequestException(clienteRequest.getIdConta())).build();
        return repository.save(cliente);
    }

    public void update(long id, ClienteRequestUpdate request) {
        Cliente savedCliente = findByIdOrThrowBadRequestException(id);
        Cliente clienteUpdated = Cliente.builder()
                .id(savedCliente.getId())
                .nome(request.getNome())
                .documento(request.getDocumento())
                .createdAt(savedCliente.getCreatedAt())
                .conta(savedCliente.getConta())
                .build();
        repository.save(clienteUpdated);
    }

    public void delete(long id) {
        repository.delete(findByIdOrThrowBadRequestException(id));
    }

}
