package com.bank.controller;

import com.bank.domain.Cliente;
import com.bank.domain.Conta;
import com.bank.request.ClienteRequest;
import com.bank.request.ClienteRequestUpdate;
import com.bank.service.ClienteService;
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

@ExtendWith(SpringExtension.class)
class ClienteControllerTest {
    public static final String IDENTIFICADOR_CONTA = "12345-6";
    @Mock
    private ClienteService clienteService;
    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setUp() {
        Mockito.when(clienteService.listAllNoPageable()).thenReturn(List.of(createCliente()));

        Mockito.when(clienteService.getAccountById(ArgumentMatchers.anyLong())).thenReturn(createConta());

        Mockito.when(clienteService.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong())).thenReturn(createCliente());

        Mockito.when(clienteService.save(ArgumentMatchers.any(ClienteRequest.class))).thenReturn(createCliente());

        Mockito.doNothing().when(clienteService).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ClienteRequestUpdate.class));

        Mockito.doNothing().when(clienteService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("listAllNonPageable returns list of clientes when successful")
    void listAllNoPageableShouldReturnsClienteOfListWhenSuccessful() {
        List<Cliente> clienteList = clienteController.listAllNoPageable().getBody();

        Assertions.assertThat(clienteList).isNotEmpty().isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("getAccountById should return conta by id when successful")
    void getAccountByIdShouldReturnContaByIdWhenSuccessful() {
        Conta contaById = clienteController.getAccountById(1L).getBody();

        Assertions.assertThat(contaById).isNotNull().isEqualTo(createConta());
        Assertions.assertThat(contaById.getId()).isEqualTo(createConta().getId());
        Assertions.assertThat(contaById.getIdentificadorConta()).isEqualTo(createConta().getIdentificadorConta());
    }

    @Test
    @DisplayName("findById should return cliente find by id when successful")
    void findByIdhouldReturnClienteFindByIdWhenSuccessful() {
        Cliente cliente = clienteController.findById(1L).getBody();

        Assertions.assertThat(cliente).isNotNull().isEqualTo(createCliente());
        Assertions.assertThat(cliente.getId()).isNotNull().isEqualTo(createCliente().getId());
        Assertions.assertThat(cliente.getDocumento()).isNotNull().isEqualTo(createCliente().getDocumento());
    }

    @Test
    @DisplayName("save should return cliente when successful")
    void saveShouldReturnClienteWhenSuccessful() {
        Cliente cliente = clienteController.save(createClienteRequest()).getBody();

        Assertions.assertThat(cliente).isNotNull().isEqualTo(createCliente());
        Assertions.assertThat(cliente.getId()).isNotNull().isEqualTo(createCliente().getId());
        Assertions.assertThat(cliente.getDocumento()).isNotNull().isEqualTo(createCliente().getDocumento());
    }

    @Test
    @DisplayName("update should update cliente when successful")
    void updateShouldUpdateClienteWhenSuccessful() {
        Assertions.assertThatCode(() -> clienteController.update(1L, createClienteRequestUpdate())).doesNotThrowAnyException();

        ResponseEntity<Void> updated = clienteController.update(1L, createClienteRequestUpdate());

        Assertions.assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete should delete cliente when successful")
    void deleteShouldDeleteClienteWhenSuccessful() {
        Assertions.assertThatCode(() -> clienteController.delete(1L)).doesNotThrowAnyException();

        ResponseEntity<Void> deleted = clienteController.delete(1L);

        Assertions.assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private static Cliente createCliente() {
        return Cliente.builder().id(1L).nome("Joao").documento("12345678901").conta(createConta())
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static Conta createConta() {
        return Conta.builder().id(1L).identificadorConta(IDENTIFICADOR_CONTA).saldo(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static ClienteRequestUpdate createClienteRequestUpdate() {
        return ClienteRequestUpdate.builder().nome("Jose").documento("12345678911").build();
    }

    private static ClienteRequest createClienteRequest() {
        return ClienteRequest.builder().nome("Saramago").documento("12345678901").idConta(createConta().getId()).build();
    }
}
