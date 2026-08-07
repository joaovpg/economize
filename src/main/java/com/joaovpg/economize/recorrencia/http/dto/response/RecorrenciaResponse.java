package com.joaovpg.economize.recorrencia.http.dto.response;

import com.joaovpg.economize.recorrencia.enums.PoliticaDataOcorrencia;
import com.joaovpg.economize.recorrencia.enums.StatusRecorrencia;
import com.joaovpg.economize.recorrencia.enums.TipoGrupoRecorrencia;
import com.joaovpg.economize.transacao.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecorrenciaResponse(
    UUID id,
    UUID grupoId,
    UUID segmentoId,
    TipoGrupoRecorrencia tipoGrupo,
    StatusRecorrencia status,
    TipoTransacao tipo,
    String descricao,
    String observacoes,
    BigDecimal valor,
    LocalDate inicio,
    LocalDate fim,
    String rrule,
    Integer totalOcorrencias,
    Integer numeroPrimeiraParcela,
    Integer quantidadeTotalOriginal,
    PoliticaDataOcorrencia politicaDataOcorrencia) {}
