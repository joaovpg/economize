package com.joaovpg.economize.categoria.application;

import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.categoria.SituacaoCategoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListarCategorias {
    private final CategoriaRepository categoriaRepository;

    public ListarCategorias(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public List<CategoriaResultado> executar(UUID usuarioId, SituacaoCategoria situacao) {
        return categoriaRepository.listarDoUsuario(usuarioId, situacao).stream()
                .map(CategoriaValidation::resultado)
                .toList();
    }
}
