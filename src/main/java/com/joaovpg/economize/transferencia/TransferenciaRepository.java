package com.joaovpg.economize.transferencia;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TransferenciaRepository implements PanacheRepositoryBase<Transferencia, UUID> {
  public Optional<Transferencia> buscarDoUsuarioParaEdicao(UUID transferenciaId, UUID usuarioId) {
    return find("id = ?1 and usuario.id = ?2", transferenciaId, usuarioId)
        .withLock(LockModeType.PESSIMISTIC_WRITE)
        .firstResultOptional();
  }

  public List<Transferencia> consultar(
      UUID usuarioId, LocalDate inicio, LocalDate fim, Set<UUID> contaIds) {
    var parametros = new HashMap<String, Object>();
    parametros.put("usuarioId", usuarioId);
    parametros.put("inicio", inicio);
    parametros.put("fim", fim);
    var filtroContas = "";
    if (!contaIds.isEmpty()) {
      filtroContas = " and (f.contaOrigem.id in :contaIds or f.contaDestino.id in :contaIds)";
      parametros.put("contaIds", contaIds);
    }
    var consulta =
        getEntityManager()
            .createQuery(
                """
                select f from Transferencia f
                join fetch f.transacaoSaida
                join fetch f.transacaoEntrada
                where f.usuario.id = :usuarioId
                  and f.dataFinanceira between :inicio and :fim
                """
                    + filtroContas
                    + " order by f.dataFinanceira, f.id",
                Transferencia.class);
    parametros.forEach(consulta::setParameter);
    return consulta.getResultList();
  }

  public BigDecimal somarImpactoAte(UUID usuarioId, LocalDate fim, Set<UUID> contaIds) {
    var saidas = somarLadoAte(usuarioId, fim, contaIds, "contaOrigem");
    var entradas = somarLadoAte(usuarioId, fim, contaIds, "contaDestino");
    return entradas.subtract(saidas);
  }

  private BigDecimal somarLadoAte(UUID usuarioId, LocalDate fim, Set<UUID> contaIds, String conta) {
    var hql =
        "select sum(f.valor) from Transferencia f "
            + "where f.usuario.id = :usuarioId and f.dataFinanceira <= :fim";
    if (!contaIds.isEmpty()) {
      hql += " and f." + conta + ".id in :contaIds";
    }
    var consulta =
        getEntityManager()
            .createQuery(hql, BigDecimal.class)
            .setParameter("usuarioId", usuarioId)
            .setParameter("fim", fim);
    if (!contaIds.isEmpty()) {
      consulta.setParameter("contaIds", contaIds);
    }
    var resultado = consulta.getSingleResult();
    return resultado == null ? BigDecimal.ZERO : resultado;
  }
}
