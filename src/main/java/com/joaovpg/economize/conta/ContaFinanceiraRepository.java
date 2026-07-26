package com.joaovpg.economize.conta;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContaFinanceiraRepository implements PanacheRepositoryBase<ContaFinanceira, UUID> {
    public Optional<ContaFinanceira> buscarAtivaDoUsuario(UUID contaId, UUID usuarioId) {
        return find("id = ?1 and usuario.id = ?2 and ativo = true", contaId, usuarioId).firstResultOptional();
    }
}
