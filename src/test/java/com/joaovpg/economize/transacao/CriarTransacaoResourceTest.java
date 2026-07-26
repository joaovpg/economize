package com.joaovpg.economize.transacao;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.joaovpg.economize.conta.ContaFinanceira;
import com.joaovpg.economize.conta.ContaFinanceiraRepository;
import com.joaovpg.economize.usuario.StatusUsuario;
import com.joaovpg.economize.usuario.Usuario;
import com.joaovpg.economize.usuario.UsuarioRepository;
import de.mkammerer.argon2.Argon2Factory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CriarTransacaoResourceTest {
    @Inject UsuarioRepository usuarioRepository;
    @Inject ContaFinanceiraRepository contaRepository;

    private String email;
    private UUID contaId;
    private UUID contaOutroUsuarioId;

    @BeforeEach
    @Transactional
    void prepararDados() {
        email = "transacao-" + UUID.randomUUID() + "@example.com";
        var argon2 = Argon2Factory.create();
        var usuario = new Usuario();
        usuario.setNome("Pessoa de teste");
        usuario.setEmail(email);
        usuario.setSenhaHash(argon2.hash(2, 19_456, 1, "senha-segura".toCharArray()));
        usuario.setTimezone("America/Sao_Paulo");
        usuario.setStatus(StatusUsuario.ATIVO);
        usuarioRepository.persist(usuario);

        var conta = new ContaFinanceira();
        conta.setUsuario(usuario);
        conta.setNome("Conta principal");
        conta.setMoeda("BRL");
        conta.setSaldoInicial(BigDecimal.ZERO);
        conta.setDataSaldoInicial(LocalDate.of(2026, 1, 1));
        contaRepository.persist(conta);
        contaRepository.flush();
        contaId = conta.getId();

        var outroUsuario = new Usuario();
        outroUsuario.setNome("Outra pessoa");
        outroUsuario.setEmail("outra-" + UUID.randomUUID() + "@example.com");
        outroUsuario.setSenhaHash(argon2.hash(2, 19_456, 1, "outra-senha".toCharArray()));
        outroUsuario.setTimezone("America/Sao_Paulo");
        outroUsuario.setStatus(StatusUsuario.ATIVO);
        usuarioRepository.persist(outroUsuario);
        var contaOutroUsuario = new ContaFinanceira();
        contaOutroUsuario.setUsuario(outroUsuario);
        contaOutroUsuario.setNome("Conta de outra pessoa");
        contaOutroUsuario.setMoeda("BRL");
        contaOutroUsuario.setSaldoInicial(BigDecimal.ZERO);
        contaOutroUsuario.setDataSaldoInicial(LocalDate.of(2026, 1, 1));
        contaRepository.persist(contaOutroUsuario);
        contaRepository.flush();
        contaOutroUsuarioId = contaOutroUsuario.getId();
    }

    @Test
    void criaTransacaoPlanejadaParaUsuarioAutenticado() {
        String token = autenticar();

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":125.4500,
                          "dataVencimento":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", equalTo("PLANEJADA"))
                .body("tipo", equalTo("DESPESA"))
                .body("descricao", equalTo("Mercado"));
    }

    @Test
    void rejeitaCriacaoSemAutenticacao() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "tipo":"DESPESA",
                          "descricao":"Mercado",
                          "valor":10.00,
                          "dataVencimento":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then().statusCode(401);
    }

    @Test
    void rejeitaCredenciaisInvalidasSemExporDetalhes() {
        given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"incorreta"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then()
                .statusCode(401)
                .body("codigo", equalTo("CREDENCIAIS_INVALIDAS"));
    }

    @Test
    void autenticaComTokenBearer() {
        given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"senha-segura"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("tipo", equalTo("Bearer"));
    }

    @Test
    void rejeitaTipoTransferencia() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "tipo":"TRANSFERENCIA",
                          "descricao":"Movimentacao",
                          "valor":10.00,
                          "dataVencimento":"2026-07-26"
                        }
                        """.formatted(contaId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(422)
                .body("codigo", equalTo("TIPO_TRANSACAO_INVALIDO"))
                .body("campos", anEmptyMap());
    }

    @Test
    void ocultaContaDeOutroUsuario() {
        given()
                .auth().oauth2(autenticar())
                .contentType("application/json")
                .body("""
                        {
                          "contaId":"%s",
                          "tipo":"DESPESA",
                          "descricao":"Movimentacao",
                          "valor":10.00,
                          "dataVencimento":"2026-07-26"
                        }
                        """.formatted(contaOutroUsuarioId))
                .when().post("/api/transacoes")
                .then()
                .statusCode(404)
                .body("codigo", equalTo("RECURSO_NAO_ENCONTRADO"));
    }

    private String autenticar() {
        return given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"senha-segura"}
                        """.formatted(email))
                .when().post("/api/autenticacao/login")
                .then().statusCode(200)
                .extract().path("token");
    }
}
