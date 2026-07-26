package com.joaovpg.economize.transacao.http;

import com.joaovpg.economize.transacao.application.CriarTransacao;
import com.joaovpg.economize.transacao.http.dto.request.CriarTransacaoRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/transacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransacaoResource {
    private final CriarTransacao criarTransacao;
    private final TransacaoHttpMapper mapper;
    private final JsonWebToken token;

    public TransacaoResource(CriarTransacao criarTransacao, TransacaoHttpMapper mapper, JsonWebToken token) {
        this.criarTransacao = criarTransacao;
        this.mapper = mapper;
        this.token = token;
    }

    @POST
    @RolesAllowed("usuario")
    public Response criar(@Valid CriarTransacaoRequest request) {
        var comando = mapper.toCommand(UUID.fromString(token.getSubject()), request);
        var resultado = criarTransacao.executar(comando);
        var response = mapper.toResponse(resultado);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
