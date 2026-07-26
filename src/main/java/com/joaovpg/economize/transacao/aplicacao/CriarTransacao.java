package com.joaovpg.economize.transacao.aplicacao;

import com.joaovpg.economize.categoria.Categoria;
import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.conta.ContaFinanceiraRepository;
import com.joaovpg.economize.transacao.RecursoFinanceiroNaoEncontradoException;
import com.joaovpg.economize.transacao.StatusTransacao;
import com.joaovpg.economize.transacao.TipoTransacao;
import com.joaovpg.economize.transacao.TipoTransacaoInvalidoException;
import com.joaovpg.economize.transacao.Transacao;
import com.joaovpg.economize.transacao.TransacaoRepository;
import com.joaovpg.economize.usuario.UsuarioRepository;
import com.joaovpg.economize.usuario.StatusUsuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class CriarTransacao {
    private final UsuarioRepository usuarioRepository;
    private final ContaFinanceiraRepository contaRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    public CriarTransacao(UsuarioRepository usuarioRepository, ContaFinanceiraRepository contaRepository,
                          CategoriaRepository categoriaRepository, TransacaoRepository transacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public Resultado executar(Comando comando) {
        validar(comando);
        var usuario = usuarioRepository.findByIdOptional(comando.usuarioId())
                .filter(candidato -> candidato.getStatus() == StatusUsuario.ATIVO)
                .orElseThrow(() -> new RecursoFinanceiroNaoEncontradoException("Usuario"));
        var conta = contaRepository.buscarAtivaDoUsuario(comando.contaId(), comando.usuarioId())
                .orElseThrow(() -> new RecursoFinanceiroNaoEncontradoException("Conta"));
        Categoria categoria = comando.categoriaId() == null ? null
                : categoriaRepository.buscarAtivaDoUsuario(comando.categoriaId(), comando.usuarioId())
                        .orElseThrow(() -> new RecursoFinanceiroNaoEncontradoException("Categoria"));
        var transacao = new Transacao();
        transacao.setUsuario(usuario);
        transacao.setConta(conta);
        transacao.setCategoria(categoria);
        transacao.setTipo(comando.tipo());
        transacao.setStatus(StatusTransacao.PLANEJADA);
        transacao.setDescricao(comando.descricao().strip());
        transacao.setObservacoes(comando.observacoes() == null ? null : comando.observacoes().strip());
        transacao.setValor(comando.valor());
        transacao.setDataVencimento(comando.dataVencimento());
        transacaoRepository.persist(transacao);
        return new Resultado(transacao.getId(), transacao.getTipo(), transacao.getStatus(),
                transacao.getDescricao(), transacao.getObservacoes(), transacao.getValor(),
                transacao.getDataVencimento(), conta.getId(), categoria == null ? null : categoria.getId());
    }

    private void validar(Comando comando) {
        if (comando.tipo() == null || comando.tipo() == TipoTransacao.TRANSFERENCIA) {
            throw new TipoTransacaoInvalidoException();
        }
        if (comando.valor() == null || comando.valor().signum() <= 0 || comando.valor().scale() > 4
                || comando.valor().setScale(4).precision() > 19) {
            throw new IllegalArgumentException("Valor da transacao invalido");
        }
        if (comando.descricao() == null || comando.descricao().isBlank()
                || comando.descricao().strip().length() > 255) {
            throw new IllegalArgumentException("Descricao da transacao invalida");
        }
        if (comando.observacoes() != null && comando.observacoes().length() > 2000) {
            throw new IllegalArgumentException("Observacoes da transacao invalidas");
        }
        if (comando.dataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento obrigatoria");
        }
    }

    public record Comando(UUID usuarioId, UUID contaId, UUID categoriaId, TipoTransacao tipo,
                          String descricao, String observacoes, BigDecimal valor, LocalDate dataVencimento) {
    }

    public record Resultado(UUID id, TipoTransacao tipo, StatusTransacao status, String descricao,
                            String observacoes, BigDecimal valor, LocalDate dataVencimento,
                            UUID contaId, UUID categoriaId) {
    }
}
