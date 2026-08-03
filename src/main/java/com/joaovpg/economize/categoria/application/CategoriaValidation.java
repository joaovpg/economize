package com.joaovpg.economize.categoria.application;

import com.joaovpg.economize.categoria.Categoria;
import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.categoria.SituacaoCategoria;
import com.joaovpg.economize.shared.exception.RegraNegocioException;
import java.util.Locale;
import java.util.UUID;

final class CategoriaValidation {
  private CategoriaValidation() {}

  static String nome(String nome) {
    if (nome == null || nome.isBlank() || nome.strip().length() > 80) {
      throw new RegraNegocioException("NOME_CATEGORIA_INVALIDO", "Nome da categoria invalido");
    }
    return nome.strip();
  }

  static String cor(String cor) {
    if (cor == null || cor.isBlank()) {
      return null;
    }
    var normalizada = cor.strip().toUpperCase(Locale.ROOT);
    if (!normalizada.matches("#[0-9A-F]{6}")) {
      throw new RegraNegocioException("COR_CATEGORIA_INVALIDA", "Cor da categoria invalida");
    }
    return normalizada;
  }

  static void nomeDisponivel(
      CategoriaRepository repository, UUID usuarioId, UUID paiId, String nome, UUID ignorarId) {
    if (repository.existeComNomeNoMesmoNivel(usuarioId, paiId, nome, ignorarId)) {
      throw nomeDuplicado();
    }
  }

  static void flush(CategoriaRepository repository) {
    try {
      repository.flush();
    } catch (org.hibernate.exception.ConstraintViolationException exception) {
      var constraint = exception.getConstraintName();
      if ("uk003_01_nome_raiz".equalsIgnoreCase(constraint)
          || "uk003_02_nome_irma".equalsIgnoreCase(constraint)) {
        throw nomeDuplicado();
      }
      throw exception;
    }
  }

  static CategoriaResultado resultado(Categoria categoria) {
    return new CategoriaResultado(
        categoria.getId(),
        categoria.getNome(),
        categoria.getCor(),
        categoria.getCategoriaPai() == null ? null : categoria.getCategoriaPai().getId(),
        situacao(categoria));
  }

  static SituacaoCategoria situacao(Categoria categoria) {
    return categoria.isAtivo() ? SituacaoCategoria.ATIVA : SituacaoCategoria.INATIVA;
  }

  private static RegraNegocioException nomeDuplicado() {
    return new RegraNegocioException(
        "NOME_CATEGORIA_DUPLICADO", "Ja existe uma categoria com esse nome no mesmo nivel");
  }
}
