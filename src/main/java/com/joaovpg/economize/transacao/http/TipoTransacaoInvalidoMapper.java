package com.joaovpg.economize.transacao.http;

import com.joaovpg.economize.compartilhado.http.ErroResponse;
import com.joaovpg.economize.transacao.TipoTransacaoInvalidoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TipoTransacaoInvalidoMapper implements ExceptionMapper<TipoTransacaoInvalidoException> {
    @Override
    public Response toResponse(TipoTransacaoInvalidoException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErroResponse("TIPO_TRANSACAO_INVALIDO", exception.getMessage()))
                .build();
    }
}
