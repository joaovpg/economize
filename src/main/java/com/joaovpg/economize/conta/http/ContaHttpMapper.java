package com.joaovpg.economize.conta.http;

import com.joaovpg.economize.conta.application.CadastrarConta;
import com.joaovpg.economize.conta.application.ContaResultado;
import com.joaovpg.economize.conta.application.EditarConta;
import com.joaovpg.economize.conta.http.dto.request.CadastrarContaRequest;
import com.joaovpg.economize.conta.http.dto.request.EditarContaRequest;
import com.joaovpg.economize.conta.http.dto.response.ContaResponse;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
interface ContaHttpMapper {
    CadastrarConta.Comando toCommand(UUID usuarioId, CadastrarContaRequest request);

    EditarConta.Comando toCommand(UUID usuarioId, UUID contaId, EditarContaRequest request);

    ContaResponse toResponse(ContaResultado resultado);
}
