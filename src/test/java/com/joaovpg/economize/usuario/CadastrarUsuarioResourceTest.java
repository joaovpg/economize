package com.joaovpg.economize.usuario;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CadastrarUsuarioResourceTest {
    @Test
    void cadastraUsuarioEIniciaSessao() {
        var email = " Pessoa-" + UUID.randomUUID() + "@Exemplo.com ";
        var emailNormalizado = email.strip().toLowerCase();

        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome":"  Maria da Silva  ",
                          "email":"%s",
                          "senha":"  senha segura  ",
                          "timezone":"America/Sao_Paulo"
                        }
                        """.formatted(email))
                .when().post("/api/autenticacao/cadastro")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("usuario.id", notNullValue())
                .body("usuario.nome", equalTo("Maria da Silva"))
                .body("usuario.email", equalTo(emailNormalizado))
                .body("usuario.timezone", equalTo("America/Sao_Paulo"));

        given()
                .contentType("application/json")
                .body("""
                        {"email":"%s","senha":"  senha segura  "}
                        """.formatted(emailNormalizado))
                .when().post("/api/autenticacao/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void rejeitaEmailJaCadastrado() {
        var email = "duplicado-" + UUID.randomUUID() + "@example.com";
        var cadastro = """
                {
                  "nome":"Maria da Silva",
                  "email":"%s",
                  "senha":"senha segura",
                  "timezone":"America/Sao_Paulo"
                }
                """.formatted(email);

        var cadastroComEmailNaoNormalizado = cadastro.replace(email, "  " + email.toUpperCase() + "  ");
        given()
                .contentType("application/json")
                .body(cadastroComEmailNaoNormalizado)
                .when().post("/api/autenticacao/cadastro")
                .then().statusCode(201);

        given()
                .contentType("application/json")
                .body(cadastro)
                .when().post("/api/autenticacao/cadastro")
                .then()
                .statusCode(409)
                .body("codigo", equalTo("EMAIL_JA_CADASTRADO"));
    }

    @Test
    void rejeitaDadosInvalidos() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome":"   ",
                          "email":"email-invalido",
                          "senha":"curta",
                          "timezone":"BRT"
                        }
                        """)
                .when().post("/api/autenticacao/cadastro")
                .then()
                .statusCode(400)
                .body("codigo", equalTo("DADOS_INVALIDOS"))
                .body("campos.nome", notNullValue())
                .body("campos.email", notNullValue())
                .body("campos.senha", notNullValue())
                .body("campos.timezone", notNullValue());

        rejeitarTimezone("EST");
        rejeitarTimezone("Etc/GMT+3");
        rejeitarTimezone("-03:00");
    }

    private void rejeitarTimezone(String timezone) {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome":"Maria da Silva",
                          "email":"timezone-%s@example.com",
                          "senha":"senha segura",
                          "timezone":"%s"
                        }
                        """.formatted(UUID.randomUUID(), timezone))
                .when().post("/api/autenticacao/cadastro")
                .then()
                .statusCode(400)
                .body("campos.timezone", notNullValue());
    }
}
