package com.joaovpg.economize.usuario.http.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {}
