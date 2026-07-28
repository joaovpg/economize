package com.joaovpg.economize.categoria.application;

import com.joaovpg.economize.categoria.SituacaoCategoria;
import java.util.UUID;

public record CategoriaResultado(UUID id, String nome, String cor, UUID categoriaPaiId,
                                 SituacaoCategoria situacao) {}
