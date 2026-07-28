package com.joaovpg.economize.categoria.http.dto.response;

import com.joaovpg.economize.categoria.SituacaoCategoria;
import java.util.UUID;

public record CategoriaResponse(UUID id, String nome, String cor, UUID categoriaPaiId,
                                SituacaoCategoria situacao) {}
