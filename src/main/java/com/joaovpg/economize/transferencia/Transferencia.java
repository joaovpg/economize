package com.joaovpg.economize.transferencia;

import com.joaovpg.economize.compartilhado.persistencia.EntidadeBase;
import com.joaovpg.economize.conta.ContaFinanceira;
import com.joaovpg.economize.transacao.Transacao;
import com.joaovpg.economize.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB007_TRANSFERENCIA")
public class Transferencia extends EntidadeBase {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTA_ORIGEM", nullable = false)
    private ContaFinanceira contaOrigem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTA_DESTINO", nullable = false)
    private ContaFinanceira contaDestino;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TRANSACAO_SAIDA", nullable = false)
    private Transacao transacaoSaida;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TRANSACAO_ENTRADA", nullable = false)
    private Transacao transacaoEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRANSFERENCIA_ESTORNADA")
    private Transferencia transferenciaEstornada;

    @Enumerated(EnumType.STRING)
    @Column(name = "STR_STATUS", nullable = false, length = 20)
    private StatusTransferencia status;

    @Column(name = "STR_DESCRICAO", nullable = false, length = 255)
    private String descricao;

    @Column(name = "STR_OBSERVACOES", length = 2000)
    private String observacoes;

    @Column(name = "DEC_VALOR", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(name = "DAT_TRANSFERENCIA", nullable = false)
    private LocalDate dataTransferencia;

    @Column(name = "DHR_EFETIVACAO")
    private Instant efetivadoEm;
}
