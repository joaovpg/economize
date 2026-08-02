package com.joaovpg.economize.usuario.http.dto.request;

import com.joaovpg.economize.usuario.http.validation.TimezoneValido;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record CadastroRequest(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Email @Size(max = 320) String email,
    @NotNull @Size(min = 8, max = 128) String senha,
    @NotBlank @Size(max = 80) @TimezoneValido String timezone) {
  public CadastroRequest {
    if (nome != null) {
      nome = nome.strip();
    }
    if (email != null) {
      email = email.strip().toLowerCase(Locale.ROOT);
    }
  }
}
