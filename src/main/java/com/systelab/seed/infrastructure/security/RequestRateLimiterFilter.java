package com.systelab.seed.infrastructure.security;

import com.systelab.seed.infrastructure.properties.ApplicationProperties;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.internal.AtomicRateLimiter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
/**
 * Filter to limit the API rate limiting at application level.
 * Filters all requests to REST API. In case we want to filter only the secured calls,
 * we should add the annotation @AuthenticationTokenNeeded at the class level.
 */
public class RequestRateLimiterFilter implements ContainerRequestFilter {

    private static final long MINUTES_DURATION = ApplicationProperties.getInstance().getRateLimitingMinutesDuration();
    private static final int LIMIT_FOR_PERIOD = ApplicationProperties.getInstance().getRateLimitingRequestLimit();

    @Context
    private HttpServletRequest httpServletRequest;

    @Inject
    private Logger logger;

    private Map<String, AtomicRateLimiter> rateLimitersMap = new HashMap<>();
    private final RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
            //Each cycle has duration configured by RateLimiterConfig.limitRefreshPeriod
            .limitRefreshPeriod(Duration.ofMinutes(MINUTES_DURATION))
            .limitForPeriod(LIMIT_FOR_PERIOD)
            .timeoutDuration(Duration.ofMillis(1))
            .build();

    /**
     * Rate limiting by username or IP.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        AtomicRateLimiter rateLimiter = getLimiter(getKey());
        if (!rateLimiter.getPermission(rateLimiterConfig.getTimeoutDuration())) {
            logLimitExceeded(requestContext.getUriInfo(), getIpAddress());
            String message = getRateExceededMessage(rateLimiter);
            throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build());
        }
    }

    private void logLimitExceeded(UriInfo uriInfo, String byAddress) {
        String path = uriInfo.getAbsolutePath().getPath();
        logger.log(Level.WARNING, "Overloaded {0} request {1}; discarding it", new Object[]{byAddress, path});
    }

    private String getRateExceededMessage(AtomicRateLimiter rateLimiter) {
        long secondsToWait = Duration.ofNanos(rateLimiter.getDetailedMetrics().getNanosToWait()).toSeconds();
        return "Maximum request rate exceeded. Wait " + secondsToWait + "s before issuing a new request";
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
        Principal principal = httpServletRequest.getUserPrincipal();
        return principal != null ? principal.getName() : getIpAddress();
    }

    private String getIpAddress() {
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        return ipAddress != null ? ipAddress : httpServletRequest.getRemoteAddr();
    }
}
