package com.joaovpg.economize.transacao;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class TransacaoRepository implements PanacheRepositoryBase<Transacao, UUID> {
    public Optional<Transacao> buscarDoUsuario(UUID id, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2", id, usuarioId).firstResultOptional();
    }

    public Optional<Transacao> buscarDoUsuarioParaEdicao(UUID id, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2", id, usuarioId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional();
    }

    public List<Transacao> consultarSimples(UUID usuarioId, LocalDate inicio, LocalDate fim,
                                            Set<UUID> contaIds, Set<UUID> categoriaIds) {
        var filtros = new ArrayList<String>();
        var parametros = new HashMap<String, Object>();
        adicionarFiltrosSimples(filtros, parametros, usuarioId, contaIds, categoriaIds);
        filtros.add("t.dataFinanceira between :inicio and :fim");
        parametros.put("inicio", inicio);
        parametros.put("fim", fim);
        var hql = "select t from Transacao t where " + String.join(" and ", filtros)
                + " order by t.dataFinanceira, t.id";
        var consulta = getEntityManager().createQuery(hql, Transacao.class);
        parametros.forEach(consulta::setParameter);
        return consulta.getResultList();
    }

    public BigDecimal somarImpactoSimplesAte(UUID usuarioId, LocalDate fim,
                                               Set<UUID> contaIds, Set<UUID> categoriaIds) {
        var filtros = new ArrayList<String>();
        var parametros = new HashMap<String, Object>();
        adicionarFiltrosSimples(filtros, parametros, usuarioId, contaIds, categoriaIds);
        filtros.add("t.dataFinanceira <= :fim");
        parametros.put("fim", fim);
        parametros.put("receita", TipoTransacao.RECEITA);

        var hql = "select sum(case when t.tipo = :receita then t.valor else -t.valor end) "
                + "from Transacao t where " + String.join(" and ", filtros);
        var consulta = getEntityManager().createQuery(hql, BigDecimal.class);
        parametros.forEach(consulta::setParameter);
        var resultado = consulta.getSingleResult();
        return resultado == null ? BigDecimal.ZERO : resultado;
    }

    private void adicionarFiltrosSimples(List<String> filtros, Map<String, Object> parametros,
                                          UUID usuarioId, Set<UUID> contaIds, Set<UUID> categoriaIds) {
        filtros.add("t.usuario.id = :usuarioId");
        filtros.add("t.tipo in :tiposSimples");
        filtros.add("t.grupoRecorrencia is null and t.segmentoRecorrencia is null");
        filtros.add("not exists (select 1 from Transferencia f where f.transacaoSaida = t or f.transacaoEntrada = t)");
        parametros.put("usuarioId", usuarioId);
        parametros.put("tiposSimples", Set.of(TipoTransacao.RECEITA, TipoTransacao.DESPESA));

        if (!contaIds.isEmpty()) {
            filtros.add("t.conta.id in :contaIds");
            parametros.put("contaIds", contaIds);
        }
        if (!categoriaIds.isEmpty()) {
            filtros.add("t.categoria.id in :categoriaIds");
            parametros.put("categoriaIds", categoriaIds);
        }
    }
}
