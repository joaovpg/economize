package com.joaovpg.economize.recorrencia;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class SupressaoRecorrenciaRepository
    implements PanacheRepositoryBase<SupressaoRecorrencia, UUID> {
  public List<SupressaoRecorrencia> listarDoUsuarioNosSegmentos(
      UUID usuarioId, Set<UUID> segmentoIds) {
    if (segmentoIds.isEmpty()) {
      return List.of();
    }
    return list("usuario.id = ?1 and segmento.id in ?2", usuarioId, segmentoIds);
  }

  public boolean existe(UUID segmentoId, LocalDate identificador, UUID usuarioId) {
    return count(
            "segmento.id = ?1 and identificadorRecorrencia = ?2 and usuario.id = ?3",
            segmentoId,
            identificador,
            usuarioId)
        > 0;
  }

  public Optional<SupressaoRecorrencia> buscarDoUsuario(
      UUID segmentoId, LocalDate identificador, UUID usuarioId) {
    return find(
            "segmento.id = ?1 and identificadorRecorrencia = ?2 and usuario.id = ?3",
            segmentoId,
            identificador,
            usuarioId)
        .firstResultOptional();
  }

  public Optional<SupressaoRecorrencia> buscarDoUsuarioParaEdicao(
      UUID segmentoId, LocalDate identificador, UUID usuarioId) {
    return find(
            "segmento.id = ?1 and identificadorRecorrencia = ?2 and usuario.id = ?3",
            segmentoId,
            identificador,
            usuarioId)
        .withLock(LockModeType.PESSIMISTIC_WRITE)
        .firstResultOptional();
  }
}
