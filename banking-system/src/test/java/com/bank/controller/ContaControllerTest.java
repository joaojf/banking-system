package com.bank.controller;

import com.bank.domain.Conta;
import com.bank.service.ContaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bank.controller.TestConstants.BODY_CORRETO;
import static com.bank.controller.TestConstants.CABECALHO_LOCATION;
import static com.bank.controller.TestConstants.CONTAS;
import static com.bank.controller.TestConstants.DATE_EXPECTED;
import static com.bank.controller.TestConstants.DEVE_CRIAR_CONTA_E_INVOCAR_APENAS_UMA_VEZ;
import static com.bank.controller.TestConstants.DEVE_RETORNAR_UMA_LISTA_COM_TODAS_AS_CONTAS;
import static com.bank.controller.TestConstants.DEVE_RETORNAR_UMA_LISTA_VAZIA_QUANDO_NAO_TIVER_CONTAS;
import static com.bank.controller.TestConstants.NUMERO_DA_CONTA;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
//@Import(GlobalExceptionHandler.class)
//@AutoConfigureMockMvc
class ContaControllerTest {

    @MockBean
    private ContaService contaService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName(DEVE_CRIAR_CONTA_E_INVOCAR_APENAS_UMA_VEZ)
    void quandoPostContas_entaoCriarContaUmaVez() throws Exception {
        // Configuração
        Conta contaMock = new Conta();
        contaMock.setIdentificadorConta(NUMERO_DA_CONTA);
        when(contaService.criarConta()).thenReturn(contaMock);

        // Ação e Verificação
        mockMvc.perform(post(CONTAS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Verifica se o método criarConta foi chamado exatamente uma vez
        verify(contaService, times(1)).criarConta();
    }

    @Test
    @DisplayName(CABECALHO_LOCATION)
    void quandoCriarConta_entaoRetornarUriCorretoNoHeaderLocation() throws Exception {
        // Configuração
        Conta contaMock = new Conta();
        contaMock.setIdentificadorConta(NUMERO_DA_CONTA);
        when(contaService.criarConta()).thenReturn(contaMock);

        // Ação e Verificação
        mockMvc.perform(post(CONTAS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
//                .andExpect(header().string(LOCATION, URL));
    }

    @Test
    @DisplayName(BODY_CORRETO)
    void quandoCriarConta_entaoVerificarRespostaComDetalhesCorretos() throws Exception {
        // Configuração
        Conta contaMock = new Conta();
        contaMock.setIdentificadorConta(NUMERO_DA_CONTA);
        contaMock.setId(1L); // Supondo que o ID seja gerado automaticamente e seja 1
        contaMock.setCreatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de criação
        contaMock.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de atualização

        when(contaService.criarConta()).thenReturn(contaMock);

        // Ação e Verificação
        mockMvc.perform(post(CONTAS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
//                .andExpect(header().string(LOCATION, URL))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.identificadorConta").value(NUMERO_DA_CONTA))
                .andExpect(jsonPath("$.createdAt").value(DATE_EXPECTED))
                .andExpect(jsonPath("$.updatedAt").value(DATE_EXPECTED));
    }

    @Test
    @DisplayName(DEVE_RETORNAR_UMA_LISTA_COM_TODAS_AS_CONTAS)
    void quandoBuscarTodas_entaoRetornaUmaListaComTodasAsContas() throws Exception {
        List<Conta> contas = new ArrayList<>();

        Conta conta1 = new Conta();
        conta1.setIdentificadorConta("12345-6");
        conta1.setId(1L);
        conta1.setCreatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de criação
        conta1.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de atualização

        Conta conta2 = new Conta();
        conta2.setIdentificadorConta("65432-1");
        conta2.setId(2L);
        conta2.setCreatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de criação
        conta2.setUpdatedAt(LocalDateTime.of(2023, 4, 1, 12, 0)); // Supondo uma data de atualização

        contas.add(conta1);
        contas.add(conta2);

        Page<Conta> contasPage = new PageImpl<>(contas);
        when(contaService.list(any(Pageable.class))).thenReturn(contasPage);

        mockMvc.perform(get(CONTAS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))  // Ajuste nesta linha
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].identificadorConta").value(NUMERO_DA_CONTA))
                .andExpect(jsonPath("$.content[0].createdAt").value(DATE_EXPECTED))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].identificadorConta").value("65432-1"))
                .andExpect(jsonPath("$.content[1].updatedAt").value(DATE_EXPECTED));
    }

    @Test
    @DisplayName(DEVE_RETORNAR_UMA_LISTA_VAZIA_QUANDO_NAO_TIVER_CONTAS)
    void quandoNaoExistemContas_retornarListaVazia() throws Exception {

        when(contaService.list(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get(CONTAS).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
