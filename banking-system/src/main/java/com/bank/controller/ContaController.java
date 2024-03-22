package com.bank.controller;

import com.bank.domain.Conta;
import com.bank.request.ContaRequest;
import com.bank.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("contas")
@RestController
public class ContaController {
    private final ContaService service;

    @GetMapping("/all")
    public ResponseEntity<List<Conta>> listAllNoPageable() {
        return ResponseEntity.ok(service.listAllNoPageable());
    }

    @GetMapping
    public ResponseEntity<Page<Conta>> listAll(Pageable pageable) {
        return new ResponseEntity<>(service.list(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> findById(@PathVariable long id) {
        return ResponseEntity.ok(service.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Conta> save() {
        return new ResponseEntity<>(service.save(), HttpStatus.CREATED);
    }

    // TODO: Nesse cenário, modificar o nome no ContaRequest para ver o erro de ConstraintViolationException
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable long id, @RequestBody @Valid ContaRequest request) {
        service.update(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
