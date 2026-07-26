package com.joaovpg.economize.transacao.http;

import com.joaovpg.economize.transacao.application.CriarTransacao;
import com.joaovpg.economize.transacao.http.dto.request.CriarTransacaoRequest;
import com.joaovpg.economize.transacao.http.dto.response.TransacaoResponse;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
interface TransacaoHttpMapper {
    CriarTransacao.Comando toCommand(UUID usuarioId, CriarTransacaoRequest request);

    TransacaoResponse toResponse(CriarTransacao.Resultado resultado);
}
