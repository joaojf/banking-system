package com.bank.controller;

import com.bank.domain.Conta;
import com.bank.request.ContaRequest;
import com.bank.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SpringExtension.class)
class ContaControllerTest {
    public static final String IDENTIFICADOR_CONTA = "12345-6";
    @Mock
    private ContaService contaService;

    @InjectMocks
    private ContaController contaController;


    @BeforeEach
    void setUp() {
        PageImpl<Conta> contaPage = new PageImpl<>(List.of(createConta()));
        Mockito.when(contaService.list(ArgumentMatchers.any())).thenReturn(contaPage);

        Mockito.when(contaService.listAllNoPageable()).thenReturn(List.of(createConta()));

        Mockito.when(contaService.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong())).thenReturn(createConta());

        Mockito.when(contaService.save()).thenReturn(createConta());

        Mockito.doNothing().when(contaService).update(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ContaRequest.class));

        Mockito.doNothing().when(contaService).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("listAllNonPageable returns list of contas when successful")
    void listAllNoPageableShouldReturnsContaOfListWhenSuccessful() {
        List<Conta> contas = contaController.listAllNoPageable().getBody();

        assertThat(contas).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    @DisplayName("listAll should returns conta of list inside page object when successful")
    void listAllShouldReturnsContaOfListInsidePageObjectWhenSuccessful() {
        Conta conta = createConta();

        Page<Conta> contasPage = contaController.listAll(null).getBody();

        assertThat(contasPage).isNotNull().isNotEmpty().hasSize(1);
        assertThat(contasPage.toList().get(0)).isEqualTo(conta);
    }

    @Test
    @DisplayName("findById should return conta find by id when successful")
    void findByIdShouldReturnContaFindByIdWhenSuccessful() {
        Long expectedContaId = createConta().getId();
        Conta actualContaById = contaController.findById(1L).getBody();

        assertThat(actualContaById).isNotNull();
        assertThat(actualContaById.getId()).isNotNull().isEqualTo(expectedContaId);
    }

    @Test
    @DisplayName("save should return conta when successful")
    void saveShouldReturnContaWhenSuccessful() {
        Conta bodyConta = contaController.save().getBody();

        assertThat(bodyConta).isNotNull().isEqualTo(createConta());
    }

    @Test
    @DisplayName("update should update conta when successful")
    void updateShouldUpdateContaWhenSuccessful() {
        assertThatCode(() -> contaController.update(1L, createContaRequest()).getBody()).doesNotThrowAnyException();

        ResponseEntity<Void> updated = contaController.update(1L, createContaRequest());

        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete should delete conta when successful")
    void deleteShouldDeleteContaWhenSuccessful() {
        assertThatCode(() -> contaController.delete(1L).getBody()).doesNotThrowAnyException();

        ResponseEntity<Void> delete = contaController.delete(1L);

        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private static Conta createConta() {
        return Conta.builder().id(1L).identificadorConta(IDENTIFICADOR_CONTA).saldo(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).build();
    }

    private static ContaRequest createContaRequest() {
        return ContaRequest.builder().identificadorConta("12345-5").build();
    }
}
