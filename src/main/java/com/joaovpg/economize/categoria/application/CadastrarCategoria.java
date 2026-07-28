package com.joaovpg.economize.categoria.application;

import com.joaovpg.economize.categoria.Categoria;
import com.joaovpg.economize.categoria.CategoriaRepository;
import com.joaovpg.economize.shared.RecursoNaoEncontradoException;
import com.joaovpg.economize.usuario.StatusUsuario;
import com.joaovpg.economize.usuario.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
public class CadastrarCategoria {
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public CadastrarCategoria(UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public CategoriaResultado executar(Comando comando) {
        var nome = CategoriaValidation.nome(comando.nome());
        var cor = CategoriaValidation.cor(comando.cor());
        var usuario = usuarioRepository.findByIdOptional(comando.usuarioId())
                .filter(candidato -> candidato.getStatus() == StatusUsuario.ATIVO)
                .orElseThrow(CadastrarCategoria::naoEncontrada);
        Categoria pai = comando.categoriaPaiId() == null ? null : categoriaRepository
                .buscarAtivaDoUsuario(comando.categoriaPaiId(), comando.usuarioId())
                .orElseThrow(CadastrarCategoria::naoEncontrada);
        CategoriaValidation.nomeDisponivel(
                categoriaRepository, comando.usuarioId(), comando.categoriaPaiId(), nome, null);

        var categoria = new Categoria();
        categoria.setUsuario(usuario);
        categoria.setCategoriaPai(pai);
        categoria.setNome(nome);
        categoria.setCor(cor);
        categoriaRepository.persist(categoria);
        CategoriaValidation.flush(categoriaRepository);
        return CategoriaValidation.resultado(categoria);
    }

    static RecursoNaoEncontradoException naoEncontrada() {
        return new RecursoNaoEncontradoException("RECURSO_NAO_ENCONTRADO", "Categoria nao encontrada");
    }

    public record Comando(UUID usuarioId, String nome, String cor, UUID categoriaPaiId) {}
}
