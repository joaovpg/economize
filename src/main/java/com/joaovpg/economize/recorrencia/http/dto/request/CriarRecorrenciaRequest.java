package com.joaovpg.economize.recorrencia.http.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.joaovpg.economize.recorrencia.enums.FrequenciaRecorrencia;
import com.joaovpg.economize.recorrencia.enums.TipoGrupoRecorrencia;
import com.joaovpg.economize.transacao.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record CriarRecorrenciaRequest(
    @NotNull TipoGrupoRecorrencia tipoGrupo,
    @NotNull UUID contaId,
    UUID categoriaId,
    @NotNull TipoTransacao tipo,
    @NotBlank String descricao,
    String observacoes,
    @NotNull @DecimalMin("0.0001") @JsonAlias("valorPorParcela") BigDecimal valor,
    @NotNull @JsonAlias({"dataInicio", "dataPrimeiraOcorrencia"}) LocalDate inicio,
    @NotNull FrequenciaRecorrencia frequencia,
    Integer intervalo,
    Set<DayOfWeek> diasSemana,
    Set<Integer> diasMes,
    @JsonAlias("count") Integer quantidadeOcorrencias,
    @JsonAlias("dataFim") LocalDate ate,
    Integer numeroPrimeiraParcela,
    Integer quantidadeTotalOriginal) {}
