package com.joaovpg.economize.recorrencia.http.dto.request;

import com.joaovpg.economize.recorrencia.enums.EscopoOcorrencia;
import com.joaovpg.economize.recorrencia.enums.FrequenciaRecorrencia;
import com.joaovpg.economize.transacao.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record AlterarOcorrenciaRecorrenteRequest(
    @NotNull EscopoOcorrencia escopo,
    @NotNull UUID contaId,
    UUID categoriaId,
    @NotNull TipoTransacao tipo,
    @NotBlank @Size(max = 255) String descricao,
    @Size(max = 2000) String observacoes,
    @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal valor,
    @NotNull LocalDate dataFinanceira,
    FrequenciaRecorrencia frequencia,
    Integer intervalo,
    Set<DayOfWeek> diasSemana,
    Set<Integer> diasMes,
    Integer quantidadeOcorrencias,
    LocalDate ate,
    Boolean semTermino,
    Integer quantidadeTotalOriginal) {}
