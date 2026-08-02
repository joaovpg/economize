package com.joaovpg.economize.transferencia.http.dto.request;

import com.joaovpg.economize.transferencia.SituacaoTransferencia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CriarTransferenciaRequest(
    @NotNull UUID contaOrigemId,
    @NotNull UUID contaDestinoId,
    @NotNull SituacaoTransferencia situacao,
    @NotBlank @Size(max = 255) String descricao,
    @Size(max = 2000) String observacoes,
    @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 15, fraction = 4) BigDecimal valor,
    @NotNull LocalDate dataFinanceira) {}
