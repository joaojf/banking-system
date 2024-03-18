package com.bank.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ContaRequest {
    public static final String NAO_PODE_ESTAR_EM_BRANCO = "O identificador da conta não pode estar em branco";

    @NotBlank(message = NAO_PODE_ESTAR_EM_BRANCO)
    private String identificadorConta;
}
