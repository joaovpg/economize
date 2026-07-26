package com.joaovpg.economize.categoria;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepositoryBase<Categoria, UUID> {
    public Optional<Categoria> buscarAtivaDoUsuario(UUID categoriaId, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2 and ativo = true", categoriaId, usuarioId).firstResultOptional();
    }
}
