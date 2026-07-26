package com.joaovpg.economize.transferencia;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class TransferenciaRepository implements PanacheRepositoryBase<Transferencia, UUID> {
}
