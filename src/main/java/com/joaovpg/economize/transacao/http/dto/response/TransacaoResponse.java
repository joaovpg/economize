package com.joaovpg.economize.transacao.http.dto.response;

import com.joaovpg.economize.transacao.StatusTransacao;
import com.joaovpg.economize.transacao.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoResponse(
        UUID id,
        TipoTransacao tipo,
        StatusTransacao status,
        String descricao,
        String observacoes,
        BigDecimal valor,
        LocalDate dataVencimento,
        UUID contaId,
        UUID categoriaId
) { }
