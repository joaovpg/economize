package com.joaovpg.economize.usuario;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

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
        .body(
            """
            {
              "nome":"  Maria da Silva  ",
              "email":"%s",
              "senha":"  senha segura  ",
              "timezone":"America/Sao_Paulo"
            }
            """
                .formatted(email))
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(201)
        .body("token", notNullValue())
        .body("usuario.id", notNullValue())
        .body("usuario.nome", equalTo("Maria da Silva"))
        .body("usuario.email", equalTo(emailNormalizado))
        .body("usuario.timezone", equalTo("America/Sao_Paulo"));

    given()
        .contentType("application/json")
        .body(
            """
            {"email":"%s","senha":"  senha segura  "}
            """
                .formatted(emailNormalizado))
        .when()
        .post("/api/autenticacao/login")
        .then()
        .statusCode(200)
        .body("token", notNullValue());
  }

  @Test
  void naoCriaCategoriaAutomatica() {
    var email = "categoria-transferencia-" + UUID.randomUUID() + "@example.com";
    String token =
        given()
            .contentType("application/json")
            .body(
                """
                {
                  "nome":"Maria da Silva",
                  "email":"%s",
                  "senha":"senha segura",
                  "timezone":"America/Sao_Paulo"
                }
                """
                    .formatted(email))
            .when()
            .post("/api/autenticacao/cadastro")
            .then()
            .statusCode(201)
            .extract()
            .path("token");

    given()
        .auth()
        .oauth2(token)
        .when()
        .get("/api/categorias")
        .then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  @Test
  void rejeitaEmailJaCadastrado() {
    var email = "duplicado-" + UUID.randomUUID() + "@example.com";
    var cadastro =
        """
        {
          "nome":"Maria da Silva",
          "email":"%s",
          "senha":"senha segura",
          "timezone":"America/Sao_Paulo"
        }
        """
            .formatted(email);

    var cadastroComEmailNaoNormalizado = cadastro.replace(email, "  " + email.toUpperCase() + "  ");
    given()
        .contentType("application/json")
        .body(cadastroComEmailNaoNormalizado)
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(201);

    given()
        .contentType("application/json")
        .body(cadastro)
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(422)
        .contentType("application/problem+json")
        .body("type", equalTo("urn:economize:problem:EMAIL_JA_CADASTRADO"))
        .body("errors", nullValue());
  }

  @Test
  void rejeitaDadosInvalidos() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "nome":"   ",
              "email":"email-invalido",
              "senha":"curta",
              "timezone":"BRT"
            }
            """)
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(400)
        .contentType("application/problem+json")
        .body("type", equalTo("urn:economize:problem:DADOS_INVALIDOS"))
        .body("errors.field", hasItems("nome", "email", "senha", "timezone"))
        .body(
            "errors.find { it.field == 'email' }.detail",
            equalTo("deve ser um endereço de e-mail bem formado"));

    rejeitarTimezone("EST");
    rejeitarTimezone("Etc/GMT+3");
    rejeitarTimezone("-03:00");
  }

  private void rejeitarTimezone(String timezone) {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "nome":"Maria da Silva",
              "email":"timezone-%s@example.com",
              "senha":"senha segura",
              "timezone":"%s"
            }
            """
                .formatted(UUID.randomUUID(), timezone))
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(400)
        .contentType("application/problem+json")
        .body("errors.find { it.field == 'timezone' }.detail", notNullValue());
  }

  @Test
  void mapeiaJsonInvalidoComoProblemDetail() {
    given()
        .contentType("application/json")
        .body("{\"nome\":")
        .when()
        .post("/api/autenticacao/cadastro")
        .then()
        .statusCode(400)
        .contentType("application/problem+json")
        .body("status", equalTo(400))
        .body("type", equalTo("urn:economize:problem:JSON_MALFORMADO"))
        .body("title", equalTo("JSON invalido"))
        .body("detail", equalTo("O corpo da requisicao contem um JSON malformado"));
  }
}
