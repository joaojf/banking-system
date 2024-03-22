package com.bank.service;

import com.bank.domain.Conta;
import com.bank.exception.BadRequestException;
import com.bank.repository.ContaRepository;
import com.bank.request.ContaRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {
    @Mock
    private ContaRepository repository;
    @InjectMocks
    private ContaService contaService;

    @Test
    @DisplayName("listAllNonPageable returns list of clientes when successful")
    void listAllNoPageableShouldReturnsContaOfListWhenSuccessful() {
        Conta conta0 = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Conta conta1 = Conta.builder().id(2L).identificadorConta("12345-7").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        List<Conta> expectedContaList = List.of(conta0, conta1);

        Mockito.when(repository.findAll()).thenReturn(expectedContaList);

        List<Conta> actualContaList = contaService.listAllNoPageable();

        Assertions.assertFalse(actualContaList.isEmpty(), "A lista de contas retornada não deve estar vazia");
        Assertions.assertEquals(expectedContaList.size(), actualContaList.size(), "O tamanho da lista de contas retornada deve ser igual ao tamanho da lista preparada");
        Assertions.assertIterableEquals(expectedContaList, actualContaList, "As listas de contas devem ser iguais");
    }

    @Test
    @DisplayName("list should returns conta of list inside page object when successful")
    void listShouldReturnsContaOfListInsidePageObjectWhenSuccessful() {
        Conta conta0 = Conta.builder().id(1L).identificadorConta("12345-6").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        Conta conta1 = Conta.builder().id(2L).identificadorConta("12345-7").saldo(BigDecimal.ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        List<Conta> expectedContaList = List.of(conta0, conta1);


        Pageable pageable = PageRequest.of(0, 2);
        PageImpl<Conta> contaPage = new PageImpl<>(expectedContaList, pageable, expectedContaList.size());
        Mockito.when(repository.findAll(pageable)).thenReturn(contaPage);

        Page<Conta> listed = contaService.list(pageable);

        assertThat(listed).isNotNull();
        assertThat(listed.getContent()).isNotEmpty().hasSize(2); // Verifica se a lista tem dois elementos
        assertThat(listed.getContent()).containsExactlyElementsOf(expectedContaList); // Verifica se os elementos na lista correspondem aos esperados
        assertThat(listed.getTotalElements()).isEqualTo(2); // Verifica se o total de elementos corresponde ao esperado
        assertThat(listed.getTotalPages()).isEqualTo(1); // Verifica se o número total de páginas corresponde ao esperado
    }


    @Test
    @DisplayName("findByIdOrThrowBadRequestException should throw BadRequestException when conta is not found")
    void findByIdOrThrowBadRequestExceptionShouldThrowBadRequestExceptionWhenContaIsNotFound() {
        Mockito.when(repository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> contaService.findByIdOrThrowBadRequestException(0L), "Deve lançar BadRequestException quando a conta não é encontrada");
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException should returns conta when successful")
    void findByIdOrThrowBadRequestExceptionShouldReturnsContaWhenSuccessful() {
        Conta expectedConta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(expectedConta));

        Conta actualConta = contaService.findByIdOrThrowBadRequestException(1L);

        Assertions.assertEquals(expectedConta, actualConta, "O resultado retornado deve ser igual a conta esperado");
        Assertions.assertDoesNotThrow(() -> contaService.findByIdOrThrowBadRequestException(1L), "O método não deve lançar uma exceção quando o ID é encontrado");
    }

    @Test
    @DisplayName("save should return the saved conta when successful")
    void saveShouldReturnSavedContaWhenSuccessful() {
        Conta expectedConta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Mockito.when(repository.save(Mockito.any(Conta.class))).thenReturn(expectedConta);

        Conta actualConta = contaService.save();

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Conta.class));

        assertThat(actualConta)
                .isNotNull()
                .isEqualTo(expectedConta)
                .extracting(Conta::getId, Conta::getIdentificadorConta, Conta::getSaldo, Conta::getCreatedAt, Conta::getUpdatedAt)
                .containsExactly(expectedConta.getId(), expectedConta.getIdentificadorConta(), expectedConta.getSaldo(), expectedConta.getCreatedAt(), expectedConta.getUpdatedAt());
    }

    @Test
    @DisplayName("update should update existing conta when successful")
    void updateShouldUpdatedContaWhenSuccessful() {
        Conta contaSaved = Conta.builder().id(1L).identificadorConta("12345-6").saldo(ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        ContaRequest contaRequest = ContaRequest.builder().identificadorConta("12345-7").build();

        Conta contaUpdate = Conta.builder().id(contaSaved.getId()).identificadorConta(contaRequest.getIdentificadorConta()).saldo(contaSaved.getSaldo()).createdAt(contaSaved.getCreatedAt())
                .updatedAt(LocalDateTime.now()).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(contaSaved));

        Mockito.when(repository.save(Mockito.any(Conta.class))).thenReturn(contaUpdate);

        contaService.update(1L, contaRequest);

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Conta.class));

        ArgumentCaptor<Conta> contaArgumentCaptor = ArgumentCaptor.forClass(Conta.class);
        Mockito.verify(repository).save(contaArgumentCaptor.capture());
        Conta contaArgumentCaptorValue = contaArgumentCaptor.getValue();

        Assertions.assertNotSame(contaSaved, contaArgumentCaptorValue, "A conta atualizado não deve ser o mesmo que a conta salvo");
    }

    @Test
    @DisplayName("update should throw BadRequestException when conta is not found")
    void updateShouldThrowBadRequestExceptionWhenContaIsNotFound() {
        Mockito.when(repository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> contaService.update(0L, Mockito.any(ContaRequest.class)), "Deve lançar BadRequestException quando a conta não é encontrada");
    }

    @Test
    @DisplayName("delete should delete existing conta when successful")
    void deleteShouldDeleteExistingContaWhenSuccessful() {
        Conta contaSaved = Conta.builder().id(1L).identificadorConta("12345-6").saldo(ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(contaSaved));

        contaService.delete(1L);

        Mockito.verify(repository).delete(contaSaved);
    }

    @Test
    @DisplayName("delete should throw BadRequestException when conta is not found")
    void deleteShouldThrowBadRequestExceptionWhenContaIsNotFound() {
        Mockito.when(repository.findById(0L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> contaService.delete(0L), "Deve lançar BadRequestException quando a conta não é encontrada");
    }

    @Test
    @DisplayName("findByIdentificadorContaOrThrowBadRequestException should return identificador when successful")
    void findByIdentificadorContaOrThrowBadRequestExceptionShouldReturnIdentificadorWhenSuccessful() {
        Conta expectedConta = Conta.builder().id(1L).identificadorConta("12345-6").saldo(ZERO).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Mockito.when(repository.findByIdentificadorConta("12345-6")).thenReturn(Optional.of(expectedConta));

        Conta actualConta = contaService.findByIdentificadorContaOrThrowBadRequestException("12345-6");

        Assertions.assertEquals(expectedConta, actualConta, "O resultado retornado deve ser igual a conta esperado");
        Assertions.assertDoesNotThrow(() -> contaService.findByIdentificadorContaOrThrowBadRequestException("12345-6"), "O método não deve lançar uma exceção quando o identificador é encontrado");
    }

    @Test
    @DisplayName("findByIdentificadorContaOrThrowBadRequestException should throw BadRequestException when identificador is not found")
    void findByIdentificadorContaOrThrowBadRequestExceptionShouldThrowBadRequestExceptionWhenIdentificadorIsNotFound() {
        Mockito.when(repository.findByIdentificadorConta("12345-6")).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () ->
                contaService.findByIdentificadorContaOrThrowBadRequestException("12345-6"), "Deve lançar BadRequestException quando o identificador não é encontrado");
    }
}
