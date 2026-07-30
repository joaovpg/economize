package com.joaovpg.economize.categoria;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.Set;

@ApplicationScoped
public class CategoriaRepository implements PanacheRepositoryBase<Categoria, UUID> {
    public Optional<Categoria> buscarAtivaDoUsuario(UUID categoriaId, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2 and ativo = true", categoriaId, usuarioId).firstResultOptional();
    }

    public Optional<Categoria> buscarDoUsuario(UUID categoriaId, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2", categoriaId, usuarioId).firstResultOptional();
    }

    public long contarDoUsuario(UUID usuarioId, Set<UUID> categoriaIds) {
        return count("usuario.id = ?1 and id in ?2", usuarioId, categoriaIds);
    }

    public boolean existeComNomeNoMesmoNivel(UUID usuarioId, UUID categoriaPaiId, String nome, UUID ignorarId) {
        if (categoriaPaiId == null) {
            if (ignorarId == null) {
                return count("usuario.id = ?1 and categoriaPai is null and lower(nome) = lower(?2)",
                        usuarioId, nome) > 0;
            }
            return count("usuario.id = ?1 and categoriaPai is null and lower(nome) = lower(?2) and id <> ?3",
                    usuarioId, nome, ignorarId) > 0;
        }
        if (ignorarId == null) {
            return count("usuario.id = ?1 and categoriaPai.id = ?2 and lower(nome) = lower(?3)",
                    usuarioId, categoriaPaiId, nome) > 0;
        }
        return count("usuario.id = ?1 and categoriaPai.id = ?2 and lower(nome) = lower(?3) and id <> ?4",
                usuarioId, categoriaPaiId, nome, ignorarId) > 0;
    }

    public List<Categoria> listarDoUsuario(UUID usuarioId, SituacaoCategoria situacao) {
        if (situacao == null) {
            return list("usuario.id = ?1 order by lower(nome), id", usuarioId);
        }
        return list("usuario.id = ?1 and ativo = ?2 order by lower(nome), id",
                usuarioId, situacao == SituacaoCategoria.ATIVA);
    }

    public boolean existeDescendenteAtiva(UUID categoriaId) {
        Number quantidade = (Number) getEntityManager().createNativeQuery("""
                WITH RECURSIVE descendentes AS (
                    SELECT ID_REGISTRO, BOL_ATIVO FROM TB003_CATEGORIA WHERE ID_CATEGORIA_PAI = :categoriaId
                    UNION ALL
                    SELECT categoria.ID_REGISTRO, categoria.BOL_ATIVO
                    FROM TB003_CATEGORIA categoria
                    JOIN descendentes pai ON categoria.ID_CATEGORIA_PAI = pai.ID_REGISTRO
                )
                SELECT COUNT(*) FROM descendentes WHERE BOL_ATIVO = TRUE
                """).setParameter("categoriaId", categoriaId).getSingleResult();
        return quantidade.longValue() > 0;
    }
}
