package com.joaovpg.economize.usuario.http;

import com.joaovpg.economize.usuario.AutenticarUsuario;
import com.joaovpg.economize.usuario.http.dto.request.LoginRequest;
import com.joaovpg.economize.usuario.http.dto.response.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
interface AutenticacaoHttpMapper {
    AutenticarUsuario.Comando toCommand(LoginRequest request);

    @Mapping(target = "tipo", constant = "Bearer")
    TokenResponse toResponse(AutenticarUsuario.Resultado resultado);
}
