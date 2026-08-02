package com.joaovpg.economize.transferencia.http.dto.response;

import com.joaovpg.economize.transferencia.SituacaoTransferencia;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransferenciaResponse(
    UUID id,
    UUID contaOrigemId,
    UUID contaDestinoId,
    SituacaoTransferencia situacao,
    String descricao,
    String observacoes,
    BigDecimal valor,
    LocalDate dataFinanceira,
    Instant efetivadoEm) {}
