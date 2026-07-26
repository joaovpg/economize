package com.joaovpg.economize.compartilhado.http;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.LinkedHashMap;

@Provider
public class ValidacaoMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var campos = new LinkedHashMap<String, String>();
        for (var violacao : exception.getConstraintViolations()) {
            var caminho = violacao.getPropertyPath().toString();
            var separador = caminho.lastIndexOf('.');
            var campo = separador < 0 ? caminho : caminho.substring(separador + 1);
            campos.putIfAbsent(campo, violacao.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("DADOS_INVALIDOS", "Um ou mais campos sao invalidos", campos))
                .build();
    }
}
