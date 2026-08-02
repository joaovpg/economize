package com.joaovpg.economize.conta;

import com.joaovpg.economize.shared.persistence.EntidadeBase;
import com.joaovpg.economize.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB002_CONTA_FINANCEIRA")
public class ContaFinanceira extends EntidadeBase {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_USUARIO", nullable = false)
  private Usuario usuario;

  @Column(name = "STR_NOME", nullable = false, length = 120)
  private String nome;

  @Column(name = "STR_MOEDA", nullable = false, length = 3)
  private String moeda;

  @Column(name = "DEC_SALDO_INICIAL", nullable = false, precision = 19, scale = 4)
  private BigDecimal saldoInicial;

  @Column(name = "DAT_SALDO_INICIAL", nullable = false)
  private LocalDate dataSaldoInicial;

  @Column(name = "BOL_ATIVO", nullable = false)
  private boolean ativo = true;

  @Column(
      name = "BOL_DADOS_INICIAIS_BLOQUEADOS",
      nullable = false,
      insertable = false,
      updatable = false)
  private boolean dadosIniciaisBloqueados;
}
