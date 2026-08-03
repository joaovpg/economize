package com.joaovpg.economize.shared.http;

import io.quarkiverse.httpproblem.HttpProblem;
import io.quarkiverse.httpproblem.postprocessing.ProblemContext;
import io.quarkiverse.httpproblem.postprocessing.ProblemPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.util.Objects;

@ApplicationScoped
public class JsonProblemPostProcessor implements ProblemPostProcessor {
  @Override
  public HttpProblem apply(HttpProblem problem, ProblemContext context) {
    if (problem.getStatusCode() != 400
        || !Objects.equals(problem.getTitle(), "Bad Request")
        || !Objects.equals(problem.getDetail(), "HTTP 400 Bad Request")) {
      return problem;
    }

    return HttpProblem.builder(problem)
        .withType(URI.create("urn:economize:problem:JSON_MALFORMADO"))
        .withTitle("JSON invalido")
        .withDetail("O corpo da requisicao contem um JSON malformado")
        .build();
  }
}
