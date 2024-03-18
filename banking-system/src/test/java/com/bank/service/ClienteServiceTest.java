package com.bank.service;

import com.bank.domain.Cliente;
import com.bank.domain.Conta;
import com.bank.repository.ClienteRepository;
import com.bank.request.ClienteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ContaService contaService;
    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("listAllNonPageable returns list of conta when successful")
    void listAllNoPageableShouldReturnsContaOfListWhenSuccessful() {
        // Preparação
        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
        Conta conta2 = new Conta(2L, "12345-7", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
        Cliente cliente1 = new Cliente(1L, "Joao", "12345678901", LocalDateTime.now(), LocalDateTime.now(), conta);
        Cliente cliente2 = new Cliente(2L, "Jose", "12345678902", LocalDateTime.now(), LocalDateTime.now(), conta2);
        List<Cliente> expectedClientes = List.of(cliente1, cliente2);
        when(clienteRepository.findAll()).thenReturn(expectedClientes);

        // Ação
        List<Cliente> result = clienteService.listAllNoPageable();

        // Verificação
        assertEquals(expectedClientes, result);
    }

    @Test
    @DisplayName("getAccountById should returns Conta when successful")
    void getAccountByIdShouldReturnsContaWhenSuccessful() {
        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
        Cliente cliente1 = new Cliente(1L, "Joao", "12345678901", LocalDateTime.now(), LocalDateTime.now(), conta);
        when(contaService.findByIdOrThrowBadRequestException(1L)).thenReturn(conta);

        Conta expectedConta = clienteService.getAccountById(1L);

        assertEquals(conta, expectedConta);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException should returns Cliente when successful")
    void findByIdOrThrowBadRequestExceptionShouldReturnsWhenSuccessful() {
        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
        Cliente expectedCliente = new Cliente(1L, "Joao", "12345678901", LocalDateTime.now(), LocalDateTime.now(), conta);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(expectedCliente));

        Cliente actualCliente = clienteService.findByIdOrThrowBadRequestException(1L);

        assertEquals(expectedCliente.getId(), actualCliente.getId());
        assertEquals(expectedCliente.getNome(), actualCliente.getNome());
        assertEquals(expectedCliente.getDocumento(), actualCliente.getDocumento());
    }

//    @Test
//    void methodShouldReturnsWhenSuccessful() {
//        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
//        ClienteRequest clienteRequest = ClienteRequest.builder().nome("Joao").documento("12345678901").idConta(conta.getId()).build();
//        when(contaService.findByIdOrThrowBadRequestException(conta.getId())).thenReturn(conta);
//        Cliente expectedCliente = new Cliente(1L, "Joao", "12345678901", LocalDateTime.now(), LocalDateTime.now(), conta);
//        when(clienteRepository.save(expectedCliente)).thenReturn(expectedCliente);
//
//        // Quando
//        Cliente actualCliente = clienteService.save(clienteRequest);
//
//        // Verificação
//        assertEquals(expectedCliente, actualCliente);
//        verify(contaService).findByIdOrThrowBadRequestException(1L);
//        verify(clienteRepository).save(expectedCliente);
//    }

//    @Test
//    void methodShouldReturnsWhenSuccessful() {
//        // Dado
//        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now());
//        ClienteRequest clienteRequest = ClienteRequest.builder().nome("Joao").documento("12345678901").idConta(conta.getId()).build();
//        when(contaService.findByIdOrThrowBadRequestException(conta.getId())).thenReturn(conta);
//        Cliente expectedCliente = Cliente.builder()
//                .id(1L)
//                .nome(clienteRequest.getNome())
//                .documento(clienteRequest.getDocumento())
//                .conta(conta)
//                .build();
//        when(clienteRepository.save(ArgumentMatchers.any())).thenReturn(expectedCliente);
//
//        // Quando
//        Cliente actualCliente = clienteService.save(clienteRequest);
//
//        // Verificação
//        verify(contaService).findByIdOrThrowBadRequestException(conta.getId()); // Verifica o ID real da conta
//        verify(clienteRepository).save(expectedCliente);
//    }


    // PASSOU
//    @Test
//    void methodShouldReturnsWhenSuccessful() {
//        // Dado
//        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.of(2023, 4, 1, 12, 0), LocalDateTime.of(2023, 4, 1, 12, 0));
//        ClienteRequest clienteRequest = ClienteRequest.builder().nome("Joao").documento("12345678901").idConta(conta.getId()).build();
//        when(contaService.findByIdOrThrowBadRequestException(conta.getId())).thenReturn(conta);
//        Cliente expectedCliente = Cliente.builder()
//                .id(null)
//                .nome(clienteRequest.getNome())
//                .documento(clienteRequest.getDocumento())
//                .conta(conta)
//                .createdAt(null)
//                .updatedAt(null)
////                .createdAt(LocalDateTime.of(2023, 4, 1, 12, 0)) // Definindo createdAt com o valor esperado
////                .updatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)) // Definindo updatedAt com o valor esperado
//                .build();
//        when(clienteRepository.save(ArgumentMatchers.any(Cliente.class))).thenReturn(expectedCliente);
//
//        // Quando
//        Cliente actualCliente = clienteService.save(clienteRequest);
//
//        // Verificação
//        verify(contaService).findByIdOrThrowBadRequestException(conta.getId()); // Verifica o ID real da conta
//        verify(clienteRepository).save(expectedCliente);
//    }

    @Test
    void methodShouldReturnsWhenSuccessful() {
        // Dado
        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.of(2023, 4, 1, 12, 0), LocalDateTime.of(2023, 4, 1, 12, 0));
        ClienteRequest clienteRequest = ClienteRequest.builder().nome("Joao").documento("12345678901").idConta(conta.getId()).build();
        when(contaService.findByIdOrThrowBadRequestException(conta.getId())).thenReturn(conta);

        // Simular conversão de ClienteRequest para Cliente
        Cliente expectedCliente = Cliente.builder()
                .id(1L) // O ID será atribuído pelo método save()
                .nome(clienteRequest.getNome())
                .documento(clienteRequest.getDocumento())
                .conta(conta)
                .createdAt(LocalDateTime.of(2023, 4, 1, 12, 0)) // O createdAt será atribuído pelo método save()
                .updatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)) // O updatedAt será atribuído pelo método save()
                .build();

        // Simular a conversão de ClienteRequest para Cliente
        when(clienteRepository.save(ArgumentMatchers.any(Cliente.class))).thenAnswer(invocation -> {
            Cliente clienteSaved = invocation.getArgument(0); // Obter o objeto Cliente passado para o método save()
            clienteSaved.setId(1L); // Definir o ID atribuído pela persistência
            clienteSaved.setCreatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Definir createdAt
            clienteSaved.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Definir updatedAt
            return clienteSaved; // Retornar o cliente modificado
        });

        // Quando
        Cliente actualCliente = clienteService.save(clienteRequest);

        // Verificação
        verify(contaService).findByIdOrThrowBadRequestException(conta.getId()); // Verificar se o método foi chamado
        verify(clienteRepository).save(expectedCliente); // Verificar se o método foi chamado com o objeto esperado
        assertNotNull(actualCliente.getId()); // Verificar se o ID do cliente retornado não é nulo
        assertEquals(1L, actualCliente.getId()); // Verificar se o ID do cliente retornado é o esperado
        assertEquals(clienteRequest.getNome(), actualCliente.getNome()); // Verificar se o nome do cliente retornado é o esperado
        assertEquals(clienteRequest.getDocumento(), actualCliente.getDocumento()); // Verificar se o documento do cliente retornado é o esperado
        assertEquals(conta, actualCliente.getConta()); // Verificar se a conta do cliente retornado é a esperada
        assertNotNull(actualCliente.getCreatedAt()); // Verificar se o createdAt do cliente retornado não é nulo
        assertNotNull(actualCliente.getUpdatedAt()); // Verificar se o updatedAt do cliente retornado não é nulo
        assertEquals(LocalDateTime.of(2023, 4, 1, 12, 0), actualCliente.getCreatedAt()); // Verificar se o createdAt do cliente retornado é o esperado
        assertEquals(LocalDateTime.of(2023, 4, 1, 12, 0), actualCliente.getUpdatedAt()); // Verificar se o updatedAt do cliente retornado é o esperado
    }

    @Test
    void mmethodShouldReturnsWhenSuccessful() {
        Conta conta = new Conta(1L, "12345-6", BigDecimal.ZERO, LocalDateTime.of(2023, 4, 1, 12, 0), LocalDateTime.of(2023, 4, 1, 12, 0));
        ClienteRequest clienteRequest = ClienteRequest.builder().nome("Joao").documento("12345678901").idConta(conta.getId()).build();
        Cliente expectedCliente = new Cliente(1L, "Joao", "12345678901", LocalDateTime.now(), LocalDateTime.now(), conta);
        when(clienteService.save(any())).thenReturn(expectedCliente);

        Cliente save = clienteService.save(clienteRequest);

        assertEquals(expectedCliente, save);
    }
}
