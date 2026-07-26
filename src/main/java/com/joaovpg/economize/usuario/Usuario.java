package com.joaovpg.economize.usuario;

import com.joaovpg.economize.compartilhado.persistencia.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB001_USUARIO")
public class Usuario extends EntidadeBase {
    @Column(name = "STR_NOME", nullable = false, length = 120)
    private String nome;

    @Column(name = "STR_EMAIL", nullable = false, length = 320)
    private String email;

    @Column(name = "STR_SENHA_HASH", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "STR_TIMEZONE", nullable = false, length = 80)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "STR_STATUS", nullable = false, length = 20)
    private StatusUsuario status;

    @Column(name = "DHR_EXCLUSAO")
    private Instant excluidoEm;
}
