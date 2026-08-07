package com.joaovpg.economize.recorrencia.http;

import com.joaovpg.economize.recorrencia.application.CriarParcelamento;
import com.joaovpg.economize.recorrencia.application.CriarRecorrencia;
import com.joaovpg.economize.recorrencia.application.GerenciarOcorrenciaRecorrente;
import com.joaovpg.economize.recorrencia.http.dto.request.AlterarOcorrenciaRecorrenteRequest;
import com.joaovpg.economize.recorrencia.http.dto.request.CriarRecorrenciaRequest;
import com.joaovpg.economize.recorrencia.http.dto.request.EfetivarOcorrenciaRecorrenteRequest;
import com.joaovpg.economize.recorrencia.http.dto.response.RecorrenciaOperacaoResponse;
import com.joaovpg.economize.recorrencia.http.dto.response.RecorrenciaResponse;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
interface RecorrenciaHttpMapper {
  CriarRecorrencia.Comando toRecorrenciaCommand(UUID usuarioId, CriarRecorrenciaRequest request);

  @Mapping(source = "request.valor", target = "valorPorParcela")
  CriarParcelamento.Comando toParcelamentoCommand(UUID usuarioId, CriarRecorrenciaRequest request);

  GerenciarOcorrenciaRecorrente.Comando toCommand(
      UUID usuarioId,
      UUID segmentoId,
      java.time.LocalDate dataOriginal,
      AlterarOcorrenciaRecorrenteRequest request);

  GerenciarOcorrenciaRecorrente.EfetivarComando toCommand(
      UUID usuarioId,
      UUID segmentoId,
      java.time.LocalDate dataOriginal,
      EfetivarOcorrenciaRecorrenteRequest request);

  @Mapping(source = "grupoId", target = "id")
  RecorrenciaResponse toResponse(CriarRecorrencia.Resultado resultado);

  @Mapping(source = "grupoId", target = "id")
  @Mapping(source = "valorPorParcela", target = "valor")
  RecorrenciaResponse toResponse(CriarParcelamento.Resultado resultado);

  @Mapping(source = "transacaoId", target = "id")
  RecorrenciaOperacaoResponse toResponse(GerenciarOcorrenciaRecorrente.Resultado resultado);
}
