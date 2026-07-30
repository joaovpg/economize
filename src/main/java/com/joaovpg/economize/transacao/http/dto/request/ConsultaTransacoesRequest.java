package com.joaovpg.economize.transacao.http.dto.request;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record ConsultaTransacoesRequest(
        YearMonth inicio,
        YearMonth fim,
        List<UUID> contaIds,
        List<UUID> categoriaIds
) { }
