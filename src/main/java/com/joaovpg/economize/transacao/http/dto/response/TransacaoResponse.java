package com.joaovpg.economize.transacao.http.dto.response;

import com.joaovpg.economize.transacao.SituacaoTransacao;
import com.joaovpg.economize.transacao.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record TransacaoResponse(
        UUID id,
        TipoTransacao tipo,
        SituacaoTransacao situacao,
        String descricao,
        String observacoes,
        BigDecimal valor,
        LocalDate dataFinanceira,
        Instant efetivadoEm,
        UUID contaId,
        UUID categoriaId
) { }
