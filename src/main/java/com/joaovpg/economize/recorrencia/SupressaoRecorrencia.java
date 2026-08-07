package com.joaovpg.economize.recorrencia;

import com.joaovpg.economize.shared.persistence.EntidadeBase;
import com.joaovpg.economize.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB008_SUPRESSAO_RECORRENCIA")
public class SupressaoRecorrencia extends EntidadeBase {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_USUARIO", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_GRUPO_RECORRENCIA", nullable = false)
  private GrupoRecorrencia grupo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_SEGMENTO_RECORRENCIA", nullable = false)
  private SegmentoRecorrencia segmento;

  @Column(name = "DAT_IDENTIFICADOR_RECORRENCIA", nullable = false)
  private java.time.LocalDate identificadorRecorrencia;
}
