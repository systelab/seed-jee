package com.systelab.seed.infrastructure.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class SecurityResponseHeadersFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        addCORSHeaders(response);
        addXSSHeaders(response);
    }

    /**
     * The HTTP X-XSS-Protection response header is a feature of Internet Explorer, Chrome and Safari that stops pages
     * from loading when they detect reflected cross-site scripting (XSS) attacks. Although these protections are largely
     * unnecessaryin modern browsers when sites implement a strong Content-Security-Policy (CSP) that disables the use of
     * inline JavaScript ('unsafe-inline'),  they can still provide protections for users of older web browsers that don't
     * yet support CSP.
     * @param response
     */
    private void addXSSHeaders(ContainerResponseContext response) {
        response.getHeaders().add( "X-XSS-Protection", "1; mode=block");
    }

    /**
     * add security response headers related with CORS (Cross-Origin Resource Sharing)
     * https://www.w3.org/TR/cors/#access-control-allow-headers-response-header
     * @param response
     */
    private void addCORSHeaders(ContainerResponseContext response) {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, Etag, if-none-match");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.getHeaders().add("Access-Control-Max-Age", "1209600");
        response.getHeaders().add("Access-Control-Expose-Headers", "origin, content-type, accept, authorization, ETag, if-none-match");
    }
}
