package com.bank.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClienteRequestUpdate {
    public static final String REGEX = "^\\d{11}$";
    public static final String NOME_BRANCO_OU_NULO = "O nome não pode estar em branco ou ser nulo";
    public static final String DOCUMENTO_BRANCO_OU_NULO = "O documento não pode estar em branco ou ser nulo";
    public static final String ONZE_DIGITOS = "O documento (CPF) deve ter exatamente 11 dígitos numéricos";

    @NotBlank(message = NOME_BRANCO_OU_NULO)
    private String nome;

    @NotBlank(message = DOCUMENTO_BRANCO_OU_NULO)
    @Pattern(regexp = REGEX, message = ONZE_DIGITOS)
    private String documento;
}
