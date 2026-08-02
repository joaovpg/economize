package com.joaovpg.economize.usuario.http.dto.response;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String email, String timezone) {}
