package com.joaovpg.economize;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Economize backend",
            description = "API para gestao financeira pessoal",
            version = "1.0.0"))
@ApplicationPath("/api")
@QuarkusMain(name = "api")
public class EconomizeApp extends Application implements QuarkusApplication {

  public static void main(String... args) {
    Quarkus.run(EconomizeApp.class, args);
  }

  @Override
  public int run(String... args) {
    Quarkus.waitForExit();
    return 0;
  }
}
