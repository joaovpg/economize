package com.joaovpg.economize.transacao.application;

import com.joaovpg.economize.shared.exception.RecursoNaoEncontradoException;
import com.joaovpg.economize.shared.exception.RegraNegocioException;
import com.joaovpg.economize.transacao.TransacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class ExcluirTransacao {
  private final TransacaoRepository transacaoRepository;

  public ExcluirTransacao(TransacaoRepository transacaoRepository) {
    this.transacaoRepository = transacaoRepository;
  }

  @Transactional
  public void executar(UUID usuarioId, UUID transacaoId) {
    var transacao =
        transacaoRepository
            .buscarDoUsuario(transacaoId, usuarioId)
            .orElseThrow(
                () ->
                    new RecursoNaoEncontradoException(
                        "RECURSO_NAO_ENCONTRADO", "Transacao nao encontrada"));

    if (transacaoRepository.estaVinculadaATransferencia(transacao.getId())
        || transacao.getGrupoRecorrencia() != null
        || transacao.getSegmentoRecorrencia() != null) {
      throw new RegraNegocioException(
          "TRANSACAO_NAO_SIMPLES", "A transacao deve ser manipulada pelo modulo de origem");
    }

    transacaoRepository.delete(transacao);
  }
}
