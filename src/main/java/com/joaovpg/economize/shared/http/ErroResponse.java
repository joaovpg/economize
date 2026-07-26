package com.joaovpg.economize.shared.http;

import java.util.Map;

public record ErroResponse(String codigo, String mensagem, Map<String, String> campos) {
    public ErroResponse(String codigo, String mensagem) {
        this(codigo, mensagem, Map.of());
    }
}
