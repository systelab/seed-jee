package com.systelab.seed.infrastructure.security;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter;
import java.io.IOException;
import java.security.Principal;
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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
/**
 * Filter to limit the API rate limiting at application level.
 * Filters all requests to REST API. In case we want to filter only the secured calls,
 * we should add the annotation @AuthenticationTokenNeeded at the class level.
 */
public class RequestRateLimiterFilter implements ContainerRequestFilter {

  @Inject
  @ConfigProperty(name = "rateLimiter.refreshPeriod.minutes", defaultValue = "1")
  private long refreshPeriodInMinutes;

  @Inject
  @ConfigProperty(name = "rateLimiter.limit", defaultValue = "100")
  private int periodToLimit;

  @Context
  private HttpServletRequest httpServletRequest;

  @Inject
  private Logger logger;

  private Map<String, AtomicRateLimiter> rateLimitersMap = new HashMap<>();

  private RateLimiterConfig rateLimiterConfig;

  /**
   * Rate limiting by username or IP.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (isLimiterEnable()) {
      String ipAddress = getIpAddress();
      AtomicRateLimiter rateLimiter = getLimiter(getKey(httpServletRequest.getUserPrincipal(), ipAddress));
      if (!rateLimiter.getPermission(rateLimiter.getRateLimiterConfig().getTimeoutDuration())) {
        logLimitExceeded(requestContext.getUriInfo(), ipAddress);
        String message = getRateExceededMessage(rateLimiter);
        throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build());
      }
    }
  }

  private AtomicRateLimiter getLimiter(String key) {
    if (rateLimiterConfig == null) {
      rateLimiterConfig = RateLimiterConfig.custom()
          .limitRefreshPeriod(Duration.ofMinutes(refreshPeriodInMinutes))
          .limitForPeriod(periodToLimit)
          .timeoutDuration(Duration.ofMillis(1))
          .build();
    }
    return rateLimitersMap.computeIfAbsent(key, k -> new AtomicRateLimiter(k, rateLimiterConfig));
  }

  private void logLimitExceeded(UriInfo uriInfo, String byAddress) {
    String path = uriInfo.getAbsolutePath().getPath();
    logger.log(Level.WARNING, "Overloaded {0} request {1}; discarding it", new Object[]{byAddress, path});
  }

  private String getRateExceededMessage(AtomicRateLimiter rateLimiter) {
    long secondsToWait = Duration.ofNanos(rateLimiter.getDetailedMetrics().getNanosToWait()).toSeconds();
    return "Maximum request rate exceeded. Wait " + secondsToWait + "s before issuing a new request";
  }

  private boolean isLimiterEnable() {
    return refreshPeriodInMinutes > 0;
  }

  /**
   * Get the key of the rateLimitersMap. In this case we check if there is a UserPrincipal to get the username or in case there is no
   * UserPrincipal when get the Ip address. An other option should be the take the JWT token or information inside token, like username.
   */
  private String getKey(Principal principal, String ipAddress) {
    return principal != null ? principal.getName() : ipAddress;
  }

  private String getIpAddress() {
    String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
    return ipAddress != null ? ipAddress : httpServletRequest.getRemoteAddr();
  }
}
