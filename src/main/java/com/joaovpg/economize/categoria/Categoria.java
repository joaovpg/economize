package com.joaovpg.economize.categoria;

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
@Table(name = "TB003_CATEGORIA")
public class Categoria extends EntidadeBase {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIA_PAI")
    private Categoria categoriaPai;

    @Column(name = "STR_NOME", nullable = false, length = 80)
    private String nome;

    @Column(name = "STR_COR", length = 20)
    private String cor;

    @Column(name = "BOL_ATIVO", nullable = false)
    private boolean ativo = true;
}
