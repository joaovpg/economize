package com.joaovpg.economize.shared.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class EntidadeBase {
  @Id
  @Column(name = "ID_REGISTRO", nullable = false, updatable = false)
  protected UUID id;

  @Column(name = "DHR_CRIACAO", nullable = false, updatable = false)
  protected Instant criadoEm;

  @Column(name = "DHR_ATUALIZACAO", nullable = false)
  protected Instant atualizadoEm;

  @Version
  @Column(name = "VER_REGISTRO", nullable = false)
  protected long versao;

  @PrePersist
  protected void prepararCriacao() {
    var now = Instant.now();
    if (id == null) {
      id = UUID.randomUUID();
    }
    criadoEm = now;
    atualizadoEm = now;
  }

  @PreUpdate
  protected void prepararAtualizacao() {
    atualizadoEm = Instant.now();
  }
}
