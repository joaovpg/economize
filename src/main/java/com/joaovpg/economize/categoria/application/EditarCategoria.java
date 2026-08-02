package com.joaovpg.economize.categoria.application;

import com.joaovpg.economize.categoria.Categoria;
import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.categoria.SituacaoCategoria;
import com.joaovpg.economize.shared.RegraNegocioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class EditarCategoria {
  private final CategoriaRepository categoriaRepository;

  public EditarCategoria(CategoriaRepository categoriaRepository) {
    this.categoriaRepository = categoriaRepository;
  }

  @Transactional
  public CategoriaResultado executar(Comando comando) {
    var categoria =
        categoriaRepository
            .buscarDoUsuario(comando.categoriaId(), comando.usuarioId())
            .orElseThrow(CadastrarCategoria::naoEncontrada);
    if (comando.situacao() == null) {
      throw new RegraNegocioException(
          "SITUACAO_CATEGORIA_INVALIDA", "Situacao da categoria invalida");
    }
    var nome = CategoriaValidation.nome(comando.nome());
    var cor = CategoriaValidation.cor(comando.cor());
    var pai = buscarEValidarPai(comando, categoria);
    CategoriaValidation.nomeDisponivel(
        categoriaRepository,
        comando.usuarioId(),
        comando.categoriaPaiId(),
        nome,
        categoria.getId());
    validarSituacao(categoria, pai, comando.situacao());

    categoria.setNome(nome);
    categoria.setCor(cor);
    categoria.setCategoriaPai(pai);
    categoria.setAtivo(comando.situacao() == SituacaoCategoria.ATIVA);
    CategoriaValidation.flush(categoriaRepository);
    return CategoriaValidation.resultado(categoria);
  }

  private Categoria buscarEValidarPai(Comando comando, Categoria categoria) {
    if (comando.categoriaPaiId() == null) {
      return null;
    }
    if (comando.categoriaPaiId().equals(categoria.getId())) {
      throw ciclo();
    }
    var pai =
        categoriaRepository
            .buscarDoUsuario(comando.categoriaPaiId(), comando.usuarioId())
            .orElseThrow(CadastrarCategoria::naoEncontrada);
    boolean mesmoPai =
        categoria.getCategoriaPai() != null
            && categoria.getCategoriaPai().getId().equals(pai.getId());
    if (CategoriaValidation.situacao(pai) == SituacaoCategoria.INATIVA && !mesmoPai) {
      throw new RegraNegocioException("CATEGORIA_PAI_INATIVA", "A categoria pai deve estar ativa");
    }
    for (var ancestral = pai; ancestral != null; ancestral = ancestral.getCategoriaPai()) {
      if (ancestral.getId().equals(categoria.getId())) {
        throw ciclo();
      }
    }
    return pai;
  }

  private void validarSituacao(Categoria categoria, Categoria novoPai, SituacaoCategoria situacao) {
    if (situacao == CategoriaValidation.situacao(categoria)) {
      return;
    }
    if (situacao == SituacaoCategoria.INATIVA
        && categoriaRepository.existeDescendenteAtiva(categoria.getId())) {
      throw new RegraNegocioException(
          "CATEGORIA_POSSUI_DESCENDENTE_ATIVA", "A categoria possui descendente ativa");
    }
    if (situacao == SituacaoCategoria.ATIVA) {
      for (var ancestral = novoPai; ancestral != null; ancestral = ancestral.getCategoriaPai()) {
        if (CategoriaValidation.situacao(ancestral) == SituacaoCategoria.INATIVA) {
          throw new RegraNegocioException(
              "CATEGORIA_POSSUI_ANCESTRAL_INATIVA", "Todos os ancestrais devem estar ativos");
        }
      }
    }
  }

  private RegraNegocioException ciclo() {
    return new RegraNegocioException(
        "HIERARQUIA_CATEGORIA_CICLICA", "A hierarquia da categoria formaria um ciclo");
  }

  public record Comando(
      UUID usuarioId,
      UUID categoriaId,
      String nome,
      String cor,
      UUID categoriaPaiId,
      SituacaoCategoria situacao) {}
}
