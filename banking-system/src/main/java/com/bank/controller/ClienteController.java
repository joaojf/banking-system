package com.bank.controller;

import com.bank.domain.Cliente;
import com.bank.domain.Conta;
import com.bank.request.ClienteRequest;
import com.bank.request.ClienteRequestUpdate;
import com.bank.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("clientes")
@RestController
public class ClienteController {
    private final ClienteService service;

    @GetMapping("/all")
    public ResponseEntity<List<Cliente>> listAllNoPageable() {
        return ResponseEntity.ok(service.listAllNoPageable());
    }

    @GetMapping("/consultar-conta/{id}")
    public ResponseEntity<Conta> getAccountById(@PathVariable long id) {
        return ResponseEntity.ok(service.getAccountById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable long id) {
        return ResponseEntity.ok(service.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> save(@RequestBody ClienteRequest clienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(clienteRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody @Valid ClienteRequestUpdate request) {
        service.update(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
