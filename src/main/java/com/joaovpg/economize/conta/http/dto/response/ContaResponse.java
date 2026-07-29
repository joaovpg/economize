package com.joaovpg.economize.conta.http.dto.response;

import com.joaovpg.economize.conta.SituacaoConta;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContaResponse(UUID id, String nome, String moeda, BigDecimal saldoInicial,
                            LocalDate dataSaldoInicial, SituacaoConta situacao) {}
