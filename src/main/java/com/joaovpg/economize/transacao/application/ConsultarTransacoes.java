package com.joaovpg.economize.transacao.application;

import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.conta.ContaFinanceiraRepository;
import com.joaovpg.economize.shared.RecursoNaoEncontradoException;
import com.joaovpg.economize.transacao.OrigemItemConsulta;
import com.joaovpg.economize.transacao.SituacaoTransacao;
import com.joaovpg.economize.transacao.TipoTransacao;
import com.joaovpg.economize.transacao.TransacaoRepository;
import com.joaovpg.economize.usuario.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class ConsultarTransacoes {
    private static final int ESCALA_MONETARIA = 4;
    private static final int MAXIMO_MESES = 12;

    private final UsuarioRepository usuarioRepository;
    private final ContaFinanceiraRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    public ConsultarTransacoes(UsuarioRepository usuarioRepository, ContaFinanceiraRepository contaRepository,
                               CategoriaRepository categoriaRepository, TransacaoRepository transacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Resultado executar(Comando comando) {
        usuarioRepository.findByIdOptional(comando.usuarioId())
                .orElseThrow(this::recursoNaoEncontrado);
        var periodo = resolverPeriodo(comando.inicio(), comando.fim());
        var contaIds = conjunto(comando.contaIds());
        var categoriaIds = conjunto(comando.categoriaIds());

        var contas = contaIds.isEmpty()
                ? contaRepository.listarDoUsuario(comando.usuarioId())
                : contaRepository.listarDoUsuario(comando.usuarioId(), contaIds);

        if (contas.size() != contaIds.size() && !contaIds.isEmpty()) {
            throw recursoNaoEncontrado();
        }

        if (!categoriaIds.isEmpty()
                && categoriaRepository.contarDoUsuario(comando.usuarioId(), categoriaIds) != categoriaIds.size()) {
            throw recursoNaoEncontrado();
        }

        var transacoes = transacaoRepository.consultarSimples(
                comando.usuarioId(),
                periodo.primeiroDia(),
                periodo.ultimoDia(),
                contaIds,
                categoriaIds);

        var saldoInicial = contas.stream()
                .filter(conta -> !conta.getDataSaldoInicial().isAfter(periodo.dataSaldoAbertura()))
                .map(conta -> conta.getSaldoInicial())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var impactoAnterior = transacaoRepository.somarImpactoSimplesAte(
                comando.usuarioId(), periodo.dataSaldoAbertura(), contaIds, categoriaIds);

        var itens = new ArrayList<Item>();
        transacoes.stream()
                .map(transacao -> new Item(OrigemItemConsulta.TRANSACAO_SIMPLES, transacao.getId(),
                        transacao.getTipo(), transacao.getSituacao(), transacao.getDescricao(),
                        transacao.getObservacoes(), transacao.getValor(), transacao.getDataFinanceira(),
                        transacao.getEfetivadoEm(), transacao.getConta().getId(),
                        transacao.getCategoria() == null ? null : transacao.getCategoria().getId()))
                .forEach(itens::add);

        contas.stream()
                .filter(conta -> !conta.getDataSaldoInicial().isBefore(periodo.primeiroDia()))
                .filter(conta -> !conta.getDataSaldoInicial().isAfter(periodo.ultimoDia()))
                .filter(conta -> conta.getSaldoInicial().signum() != 0)
                .map(conta -> new Item(
                        OrigemItemConsulta.SALDO_INICIAL_CONTA,
                        conta.getId(),
                        conta.getSaldoInicial().signum() > 0 ? TipoTransacao.RECEITA : TipoTransacao.DESPESA,
                        null,
                        "Saldo inicial",
                        null,
                        conta.getSaldoInicial().abs(),
                        conta.getDataSaldoInicial(),
                        null,
                        conta.getId(),
                        null))
                .forEach(itens::add);

        itens.sort(Comparator.comparing(Item::dataFinanceira)
                .thenComparingInt(item -> item.origem() == OrigemItemConsulta.SALDO_INICIAL_CONTA ? 0 : 1)
                .thenComparing(Item::operacaoId));

        return new Resultado(
                periodo.inicio(),
                periodo.fim(),
                monetario(saldoInicial.add(impactoAnterior)),
                List.copyOf(itens));
    }

    private Periodo resolverPeriodo(YearMonth inicio, YearMonth fim) {
        if (inicio == null || fim == null) {
            throw new ConsultaTransacoesInvalidaException("periodo", "Inicio e fim devem ser informados juntos");
        }

        if (inicio.isAfter(fim)) {
            throw new ConsultaTransacoesInvalidaException("periodo", "Inicio deve ser anterior ou igual ao fim");
        }

        if (ChronoUnit.MONTHS.between(inicio, fim) + 1 > MAXIMO_MESES) {
            throw new ConsultaTransacoesInvalidaException("periodo", "O periodo deve ter no maximo 12 meses");
        }

        return new Periodo(inicio, fim);
    }

    private <T> Set<T> conjunto(List<T> valores) {
        return valores == null ? Set.of() : new LinkedHashSet<>(valores);
    }

    private BigDecimal monetario(BigDecimal valor) {
        return valor.setScale(ESCALA_MONETARIA, RoundingMode.UNNECESSARY);
    }

    private RecursoNaoEncontradoException recursoNaoEncontrado() {
        return new RecursoNaoEncontradoException("RECURSO_NAO_ENCONTRADO", "Recurso nao encontrado");
    }

    private record Periodo(YearMonth inicio, YearMonth fim) {
        private LocalDate primeiroDia() {
            return inicio.atDay(1);
        }

        private LocalDate ultimoDia() {
            return fim.atEndOfMonth();
        }

        private LocalDate dataSaldoAbertura() {
            return primeiroDia().minusDays(1);
        }
    }

    public record Comando(UUID usuarioId, YearMonth inicio, YearMonth fim,
                          List<UUID> contaIds, List<UUID> categoriaIds) { }

    public record Resultado(YearMonth inicio, YearMonth fim, BigDecimal saldoAbertura, List<Item> itens) { }

    public record Item(OrigemItemConsulta origem, UUID operacaoId, TipoTransacao tipo,
                       SituacaoTransacao situacao, String descricao, String observacoes,
                       BigDecimal valor, LocalDate dataFinanceira, java.time.Instant efetivadoEm,
                       UUID contaId, UUID categoriaId) { }

}
