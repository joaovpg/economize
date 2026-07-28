package com.joaovpg.economize.categoria.http.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record CadastrarCategoriaRequest(
        @NotBlank String nome,
        @Pattern(regexp = "^\\s*(#[0-9A-Fa-f]{6})?\\s*$") String cor,
        UUID categoriaPaiId) {}
