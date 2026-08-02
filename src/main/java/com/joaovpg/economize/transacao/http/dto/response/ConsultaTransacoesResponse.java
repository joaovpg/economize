package com.joaovpg.economize.transacao.http.dto.response;

import com.joaovpg.economize.transacao.OrigemItemConsulta;
import com.joaovpg.economize.transacao.SituacaoTransacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record ConsultaTransacoesResponse(
    YearMonth inicio, YearMonth fim, BigDecimal saldoAbertura, List<ItemResponse> itens) {
  public record ItemResponse(
      OrigemItemConsulta origem,
      UUID operacaoId,
      SituacaoTransacao situacao,
      String descricao,
      String observacoes,
      BigDecimal valor,
      LocalDate dataFinanceira,
      Instant efetivadoEm,
      UUID contaId,
      UUID categoriaId,
      UUID contaContraparteId) {}
}
