package com.joaovpg.economize.recorrencia;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class GrupoRecorrenciaRepository implements PanacheRepositoryBase<GrupoRecorrencia, UUID> {
}
