package com.bank.controller;

public class TestConstants {
    public static final String DEVE_CRIAR_CONTA_E_INVOCAR_APENAS_UMA_VEZ = "POST /contas A chamada ao endpoint PostContas deve resultar na criação de uma conta, e o método criarConta() do serviço deve ser invocado exatamente uma vez";
    public static final String STATUS_500_EM_CASO_DE_FALHA_INTERNA = "POST /contas deve retornar status 500 em caso de falha interna";
    public static final String FALHA_AO_CRIAR_CONTA = "Falha ao criar conta";
    public static final String CONTAS = "/contas";
    public static final String NUMERO_DA_CONTA = "12345-6";
    public static final String CABECALHO_LOCATION = "POST /contas deve retornar o URI correto no cabeçalho 'Location'";
    public static final String LOCATION = "Location";
    public static final String URL = "http://localhost/contas/12345-6";
    public static final String DATE_EXPECTED = "2023-04-01T12:00:00";
    public static final String BODY_CORRETO = "POST /contas Quando criar uma conta, então a resposta deve conter os detalhes corretos";
    public static final String DEVE_RETORNAR_UMA_LISTA_COM_TODAS_AS_CONTAS = "GET /contas deve retornar uma lista com todas as contas";
    public static final String DEVE_RETORNAR_UMA_LISTA_VAZIA_QUANDO_NAO_TIVER_CONTAS = "GET /contas deve retornar uma lista vazia quando não existir nenhuma conta";
}
