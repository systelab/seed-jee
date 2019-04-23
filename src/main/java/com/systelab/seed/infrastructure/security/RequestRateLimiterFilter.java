package com.systelab.seed.infrastructure.security;

import com.systelab.seed.infrastructure.properties.ApplicationProperties;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


@Provider
/**
 * Filter to limit the API rate limiting at application level.
 * Filters all requests to REST API. In case we want to filter only the secured calls,
 * we should add the annotation @AuthenticationTokenNeeded at the class level.
 */
public class RequestRateLimiterFilter implements ContainerRequestFilter {

  private static final long MINUTES_DURATION = ApplicationProperties.getInstance()
      .getRateLimitingMinutesDuration();
  private static final int LIMIT_FOR_PERIOD = ApplicationProperties.getInstance()
      .getRateLimitingRequestLimit();

  @Context
  private HttpServletRequest httpServletRequest;

  private Logger logger;

  private Map<String, AtomicRateLimiter> rateLimitersMap = new HashMap<>();
  private final RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
      //Each cycle has duration configured by RateLimiterConfig.limitRefreshPeriod
      .limitRefreshPeriod(Duration.ofMinutes(MINUTES_DURATION))
      .limitForPeriod(LIMIT_FOR_PERIOD)
      .timeoutDuration(Duration.ofMillis(1))
      .build();


  @Inject
  public void setLogger(Logger logger) {
    this.logger = logger;
  }


  /**
   * Rate limiting by username or IP.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    AtomicRateLimiter rateLimiter = getLimiter(getKey());
    if (!rateLimiter.getPermission(rateLimiterConfig.getTimeoutDuration())) {
      long secondsToWait = Duration.ofNanos(rateLimiter.getDetailedMetrics().getNanosToWait())
          .toSeconds();
      URI absolutePath = requestContext.getUriInfo().getAbsolutePath();
      String path = absolutePath.getPath();
      String message =
          "Maximum request rate exceeded. Wait " + secondsToWait + "s before issuing a new request";
      logger.log(Level.WARNING, "Overloaded {0} request {1}; discarding it",
          new Object[]{getIpAddress(), path});
      throw new WebApplicationException(
          Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build());
    }
  }

  private AtomicRateLimiter getLimiter(String key) {
    return rateLimitersMap.computeIfAbsent(key, k -> new AtomicRateLimiter(k, rateLimiterConfig));
  }

  /**
   * Get the key of the rateLimitersMap. In this case we check if there is a UserPrincipal to get
   * the username or in case there is no UserPrincipal when get the Ip address. An other option
   * should be the take the JWT token or information inside token, like username.
   */
  private String getKey() {
    String key;
    if (httpServletRequest.getUserPrincipal() != null) {
      key = httpServletRequest.getUserPrincipal().getName();
    } else {
      key = getIpAddress();
    }
    return key;
  }

  private String getIpAddress() {
    String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = httpServletRequest.getRemoteAddr();
    }
    return ipAddress;
  }

}
