package com.bank.service;

import com.bank.domain.Cliente;
import com.bank.domain.Conta;
import com.bank.exception.BadRequestException;
import com.bank.repository.ClienteRepository;
import com.bank.request.ClienteRequest;
import com.bank.request.ClienteRequestUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock
    private ClienteRepository repository;
    @Mock
    private ContaService contaService;
    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("listAllNonPageable returns list of clientes when successful")
    void listAllNoPageableShouldReturnsClienteOfListWhenSuccessful() {
        Conta conta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Conta conta1 = Conta.builder().id(2L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Cliente cliente = Cliente.builder().id(1L).nome("Joao").documento("12345678901").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(conta).build();
        Cliente cliente1 = Cliente.builder().id(2L).nome("Jose").documento("12345678902").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(conta1).build();

        List<Cliente> clientesExpected = List.of(cliente, cliente1);

        Mockito.when(repository.findAll()).thenReturn(clientesExpected);

        List<Cliente> resultActual = clienteService.listAllNoPageable();

        Assertions.assertFalse(resultActual.isEmpty(), "A lista de clientes retornada não deve estar vazia");
        Assertions.assertEquals(clientesExpected.size(), resultActual.size(), "O tamanho da lista de clientes retornada deve ser igual ao tamanho da lista preparada");
        Assertions.assertIterableEquals(clientesExpected, resultActual, "As listas de clientes devem ser iguais");
    }

    @Test
    @DisplayName("getAccountById should returns conta when successful")
    void getAccountByIdShouldReturnsContaWhenSuccessful() {
        Conta contaExpected = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Mockito.when(contaService.findByIdOrThrowBadRequestException(1L)).thenReturn(contaExpected);

        Conta resultActual = clienteService.getAccountById(1L);

        Mockito.verify(contaService).findByIdOrThrowBadRequestException(1L);
        Mockito.verify(contaService, Mockito.times(1)).findByIdOrThrowBadRequestException(1L);

        Assertions.assertNotNull(resultActual, "O resultado retornado não deve ser nulo");
        Assertions.assertEquals(contaExpected, resultActual, "O resultado retornado deve ser igual à conta esperada");
        Assertions.assertEquals(contaExpected.getIdentificadorConta(), resultActual.getIdentificadorConta(), "O identificador da conta deve ser o esperado");
        Assertions.assertEquals(contaExpected.getSaldo(), resultActual.getSaldo(), "O saldo da conta deve ser o esperado");

    }

    @Test
    @DisplayName("getAccountById should throw BadRequestException when conta is not found")
    void getAccountByIdShouldThrowBadRequestExceptionWhenContaIsNotFound() {
        Mockito.when(contaService.findByIdOrThrowBadRequestException(2L)).thenThrow(BadRequestException.class);

        Assertions.assertThrows(BadRequestException.class, () -> clienteService.getAccountById(2L), "Deve lançar BadRequestException quando a conta não é encontrada");
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException should returns cliente when successful")
    void findByIdOrThrowBadRequestExceptionShouldReturnClienteWhenSuccessful() {
        Conta conta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Cliente clienteExpected = Cliente.builder().id(1L).nome("Joao").documento("12345678901").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(conta).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(clienteExpected));

        Cliente resultActual = clienteService.findByIdOrThrowBadRequestException(1L);

        Assertions.assertEquals(clienteExpected, resultActual, "O resultado retornado deve ser igual ao cliente esperado");
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException should throw BadRequestException when cliente is not found")
    void findByIdOrThrowBadRequestExceptionShouldThrowBadRequestExceptionWhenClienteIsNotFound() {
        Mockito.when(repository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> clienteService.findByIdOrThrowBadRequestException(2L), "Deve lançar BadRequestException quando o cliente não é encontrado");
    }

    @Test
    @DisplayName("save should return the saved Cliente when successful")
    void saveShouldReturnSavedClienteWhenSuccessful() {
        Conta conta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        ClienteRequest clienteRequest = ClienteRequest.builder()
                .nome("Joao")
                .documento("12345678901")
                .idConta(conta.getId())
                .build();

        Cliente clienteExpected = Cliente.builder()
                .id(1L)
                .nome("Joao")
                .documento("12345678901")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .conta(conta)
                .build();

        Mockito.when(contaService.findByIdOrThrowBadRequestException(1L)).thenReturn(conta);
        Mockito.when(repository.save(Mockito.any(Cliente.class))).thenReturn(clienteExpected);

        Cliente resultActual = clienteService.save(clienteRequest);

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Cliente.class));

        Assertions.assertEquals(clienteExpected, resultActual, "O cliente retornado deve ser igual ao cliente esperado");
        Assertions.assertEquals(clienteExpected.getId(), resultActual.getId(), "Os ids devem ser iguais");
    }

    @Test
    @DisplayName("save should throw BadRequestException when conta is not found")
    void saveShouldThrowBadRequestExceptionWhenContaIsNotFound() {
        Mockito.when(contaService.findByIdOrThrowBadRequestException(0L)).thenThrow(BadRequestException.class);

        ClienteRequest clienteRequest = ClienteRequest.builder()
                .nome("Joao")
                .documento("12345678901")
                .idConta(0L)
                .build();

        Assertions.assertThrows(BadRequestException.class, () -> clienteService.save(clienteRequest), "Deve lançar BadRequestException quando a conta não é encontrada");
    }

    @Test
    @DisplayName("update should update existing cliente when successful")
    void updateShouldUpdatedClienteWhenSuccessful() {
        Conta conta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        ClienteRequestUpdate clienteRequest = ClienteRequestUpdate.builder()
                .nome("Jose")
                .documento("12345678902")
                .build();

        Cliente clienteSaved = Cliente.builder()
                .id(1L)
                .nome("Joao")
                .documento("12345678901")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .conta(conta)
                .build();

        Cliente clienteUpdated = Cliente.builder()
                .id(clienteSaved.getId())
                .nome(clienteRequest.getNome())
                .documento(clienteRequest.getDocumento())
                .createdAt(clienteSaved.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .conta(conta)
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(clienteSaved));

        Mockito.when(repository.save(Mockito.any(Cliente.class))).thenReturn(clienteUpdated);

        clienteService.update(1L, clienteRequest);

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Cliente.class));

        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        Mockito.verify(repository).save(clienteCaptor.capture());
        Cliente clienteAtualizado = clienteCaptor.getValue();

        Assertions.assertNotSame(clienteSaved, clienteAtualizado, "O cliente atualizado não deve ser o mesmo que o cliente salvo");
        Assertions.assertEquals(clienteSaved.getId(), clienteAtualizado.getId(), "O ID do cliente não deve ser alterado durante a atualização");
        Assertions.assertEquals(clienteRequest.getNome(), clienteAtualizado.getNome(), "O nome do cliente deve ser atualizado corretamente");
        Assertions.assertEquals(clienteRequest.getDocumento(), clienteAtualizado.getDocumento(), "O documento do cliente deve ser atualizado corretamente");
        Assertions.assertEquals(clienteSaved.getCreatedAt(), clienteAtualizado.getCreatedAt(), "O createdAt do cliente deve ser igual do cliente atualizado");
        Assertions.assertNotEquals(clienteSaved.getUpdatedAt(), clienteUpdated.getUpdatedAt(), "O updatedAt do cliente deve ser diferente do cliente atualizado");
    }

    @Test
    @DisplayName("update should throw BadRequestException when cliente is not found")
    void updateShouldThrowBadRequestExceptionWhenClienteIsNotFound() {
        long nonExistingId = 0L;

        Mockito.when(repository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> clienteService.update(nonExistingId,
                        Mockito.any(ClienteRequestUpdate.class)),
                "Deve lançar BadRequestException quando o cliente não é encontrado");
    }

    @Test
    @DisplayName("delete should delete existing cliente when successful")
    void deleteShouldDeleteExistingClienteWhenSuccessful() {
        long existingId = 1L;

        Conta conta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Cliente clienteExpected = Cliente.builder().id(1L).nome("Joao").documento("12345678901").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).conta(conta).build();

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(clienteExpected));

        clienteService.delete(existingId);

        Mockito.verify(repository).delete(clienteExpected);
    }

    @Test
    @DisplayName("delete should throw BadRequestException when cliente is not found")
    void deleteShouldThrowBadRequestExceptionWhenClienteIsNotFound() {
        long nonExistingId = 0L;

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> clienteService.delete(nonExistingId), "Deve lançar BadRequestException quando o cliente não é encontrado");
    }
}