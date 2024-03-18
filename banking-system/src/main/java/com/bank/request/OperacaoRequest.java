package com.bank.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OperacaoRequest {
    public static final String NAO_PODE_ESTAR_EM_BRANCO = "O identificador da conta não pode estar em branco";
    public static final String O_VALOR_NAO_PODE_ESTA_EM_BRANCO = "O valor não pode está em branco";

    @NotBlank(message = NAO_PODE_ESTAR_EM_BRANCO)
    private String identificador;

    @NotBlank(message = O_VALOR_NAO_PODE_ESTA_EM_BRANCO)
    private BigDecimal valor;
}
