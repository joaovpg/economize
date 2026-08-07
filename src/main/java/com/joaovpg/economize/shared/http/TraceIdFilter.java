package com.joaovpg.economize.shared.http;

import io.quarkiverse.httpproblem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import java.util.Objects;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

@Provider
@Priority(Priorities.USER)
public class TraceIdFilter implements ContainerRequestFilter, ContainerResponseFilter {
  static final String PROPERTY = TraceIdFilter.class.getName() + ".traceId";
  static final String START_PROPERTY = TraceIdFilter.class.getName() + ".start";
  static final String HEADER = "X-Trace-Id";
  private static final Logger LOG = Logger.getLogger(TraceIdFilter.class);

  @Context ResourceInfo resourceInfo;

  @ConfigProperty(name = "economize.logging.threshold-millis", defaultValue = "1000")
  long slowRequestThresholdMillis;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    var traceId = UUID.randomUUID().toString();
    requestContext.setProperty(PROPERTY, traceId);
    requestContext.setProperty(START_PROPERTY, System.nanoTime());
    MDC.put("traceId", traceId);
  }

  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    var traceId = (String) requestContext.getProperty(PROPERTY);
    if (traceId == null) {
      return;
    }

    try {
      responseContext.getHeaders().putSingle(HEADER, traceId);
      if (responseContext.getEntity() instanceof HttpProblem problem) {
        responseContext.setEntity(HttpProblem.builder(problem).with("traceId", traceId).build());
      }

      var usuarioId = usuarioId(requestContext);
      if (usuarioId != null) {
        MDC.put("usuarioId", usuarioId);
      }

      var durationMillis = durationMillis(requestContext);
      var status = responseContext.getStatus();
      var message =
          "HTTP "
              + requestContext.getMethod()
              + " "
              + routeTemplate()
              + " status="
              + status
              + " durationMs="
              + durationMillis;

      if (status >= 500) {
        LOG.error(message);
      } else if (status >= 400) {
        LOG.warn(message);
      } else {
        LOG.info(message);
      }

      if (durationMillis >= slowRequestThresholdMillis) {
        LOG.warn("Requisicao HTTP lenta: " + message);
      }
    } finally {
      MDC.remove("usuarioId");
      MDC.remove("traceId");
    }
  }

  private long durationMillis(ContainerRequestContext requestContext) {
    var start = requestContext.getProperty(START_PROPERTY);
    if (!(start instanceof Long startNanos)) {
      return 0;
    }
    return (System.nanoTime() - startNanos) / 1_000_000;
  }

  private String usuarioId(ContainerRequestContext requestContext) {
    var securityContext = requestContext.getSecurityContext();
    if (securityContext == null || securityContext.getUserPrincipal() == null) {
      return null;
    }

    var principal = securityContext.getUserPrincipal().getName();
    try {
      return UUID.fromString(principal).toString();
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  private String routeTemplate() {
    if (resourceInfo == null || resourceInfo.getResourceClass() == null) {
      return "/api/<unmatched>";
    }

    var classPath = resourceInfo.getResourceClass().getAnnotation(jakarta.ws.rs.Path.class);
    var method = resourceInfo.getResourceMethod();
    var methodPath = method == null ? null : method.getAnnotation(jakarta.ws.rs.Path.class);
    return "/api" + path(classPath) + path(methodPath);
  }

  private String path(jakarta.ws.rs.Path path) {
    if (path == null || Objects.equals(path.value(), "/")) {
      return "";
    }
    return "/" + path.value().replaceAll("^/+|/+$", "");
  }
}
