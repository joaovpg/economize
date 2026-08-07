package com.joaovpg.economize.recorrencia.http.dto.response;

import com.joaovpg.economize.recorrencia.enums.PoliticaDataOcorrencia;
import com.joaovpg.economize.recorrencia.enums.StatusRecorrencia;
import com.joaovpg.economize.recorrencia.enums.TipoGrupoRecorrencia;
import com.joaovpg.economize.transacao.SituacaoTransacao;
import com.joaovpg.economize.transacao.TipoTransacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RecorrenciaOperacaoResponse(
    UUID id,
    UUID grupoId,
    UUID segmentoId,
    TipoGrupoRecorrencia tipoGrupo,
    StatusRecorrencia status,
    TipoTransacao tipo,
    SituacaoTransacao situacao,
    String descricao,
    String observacoes,
    BigDecimal valor,
    LocalDate dataFinanceira,
    Instant efetivadoEm,
    UUID contaId,
    UUID categoriaId,
    LocalDate dataOriginalRecorrencia,
    Integer numeroParcela,
    String rrule,
    LocalDate inicioRecorrencia,
    PoliticaDataOcorrencia politicaDataOcorrencia) {}
