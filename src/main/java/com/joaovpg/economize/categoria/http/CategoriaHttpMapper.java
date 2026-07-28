package com.joaovpg.economize.categoria.http;

import com.joaovpg.economize.categoria.application.CadastrarCategoria;
import com.joaovpg.economize.categoria.application.CategoriaResultado;
import com.joaovpg.economize.categoria.application.EditarCategoria;
import com.joaovpg.economize.categoria.http.dto.request.CadastrarCategoriaRequest;
import com.joaovpg.economize.categoria.http.dto.request.EditarCategoriaRequest;
import com.joaovpg.economize.categoria.http.dto.response.CategoriaResponse;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
interface CategoriaHttpMapper {
    CadastrarCategoria.Comando toCommand(UUID usuarioId, CadastrarCategoriaRequest request);

    EditarCategoria.Comando toCommand(UUID usuarioId, UUID categoriaId, EditarCategoriaRequest request);

    CategoriaResponse toResponse(CategoriaResultado resultado);
}
