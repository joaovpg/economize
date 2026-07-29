package com.joaovpg.economize.conta.http.dto.request;

import com.joaovpg.economize.conta.SituacaoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EditarContaRequest(
        @NotBlank String nome,
        @NotBlank String moeda,
        @NotNull BigDecimal saldoInicial,
        @NotNull LocalDate dataSaldoInicial,
        @NotNull SituacaoConta situacao) {}
