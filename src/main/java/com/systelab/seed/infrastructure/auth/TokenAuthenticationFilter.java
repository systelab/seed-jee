package com.systelab.seed.infrastructure.auth;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@AuthenticationTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class TokenAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private Logger logger;

    @Inject
    private AuthenticationTokenGenerator tokenGenerator;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        logger.info(requestContext.getUriInfo().getRequestUri().toString());

        String token = getTokenFromHeader(requestContext);
        if (token != null) {
            try {
                String userRole = tokenGenerator.validateToken(token);
                if (!methodIsAllowed(resourceInfo.getResourceMethod(), userRole)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Invalid token : " + token, ex);
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } else {
            logger.severe("Invalid authorizationHeader");
            throw new NotAuthorizedException("Valid authorization header must be provided");
        }
    }

    private String getTokenFromHeader(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer".length()).trim();
        } else {
            return null;
        }
    }

    private boolean methodIsAllowed(Method method, String userRole) {
        if (method.isAnnotationPresent(DenyAll.class)) {
            return false;
        } else if (method.isAnnotationPresent(PermitAll.class)) {
            return true;
        } else if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
            Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
            return rolesSet.contains(userRole);
        } else {
            return true;
        }
    }
}