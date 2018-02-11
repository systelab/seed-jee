package com.systelab.seed.infrastructure.security;

import javax.servlet.annotation.WebFilter;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class XFrameOptionsFilter implements Filter {

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        // Not used
    }

    @Override
    public void destroy() {
        // Not used
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.addHeader("X-Frame-Options", "deny");
        }
        chain.doFilter(request, response);
    }
}