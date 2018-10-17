package com.systelab.seed.infrastructure.security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class ContentSecurityPolicyFilter implements Filter {

    public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";

    public static final String REPORT_URI = "report-uri";

    /**
     * Enables a sandbox for the requested resource similar to the iframe sandbox attribute.
     * The sandbox applies a same origin policy, prevents popups, plugins and script execution is blocked.
     * You can keep the sandbox value empty to keep all restrictions in place, or add values:
     * allow-forms allow-same-origin allow-scripts, and allow-top-navigation
     */
    public static final String SANDBOX = "sandbox";
    // The default policy for loading content such as JavaScript, Images, CSS, Font's, AJAX requests, Frames, HTML5 Media 
    public static final String DEFAULT_SRC = "default-src";
    // Defines valid sources of images 
    public static final String IMG_SRC = "img-src";
    // Defines valid sources of JavaScript  
    public static final String SCRIPT_SRC = "script-src";
    // Defines valid sources of stylesheets 
    public static final String STYLE_SRC = "style-src";
    // Defines valid sources of fonts 
    public static final String FONT_SRC = "font-src";
    // Applies to XMLHttpRequest (AJAX), WebSocket or EventSource 
    public static final String CONNECT_SRC = "connect-src";
    // Defines valid sources of plugins, eg <object>, <embed> or <applet>.  
    public static final String OBJECT_SRC = "object-src";
    // Defines valid sources of audio and video, eg HTML5 <audio>, <video> elements 
    public static final String MEDIA_SRC = "media-src";
    // Defines valid sources for loading frames 
    public static final String FRAME_SRC = "frame-src";

    public static final String SELF_REFERENCE= "'self'";



    private String reportUri;
    private String sandboxValue;
    private String defaultSrc;
    private String imgSrc;
    private String scriptSrc;
    private String styleSrc;
    private String fontSrc;
    private String connectSrc;
    private String objectSrc;
    private String mediaSrc;
    private String frameSrc;
    /**
     * Used to prepare (one time for all) set of CSP policies that will be applied on each HTTP response.
     */
    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        reportUri = "/ContentSecurityPolicyReporter";
        sandboxValue = "";
        defaultSrc = "none";
        imgSrc = SELF_REFERENCE+" data: online.swagger.io";
        scriptSrc = SELF_REFERENCE+" 'unsafe-inline' ";
        styleSrc = SELF_REFERENCE+" 'unsafe-inline' fonts.googleapis.com";
        fontSrc = SELF_REFERENCE+" fonts.gstatic.com";
        connectSrc = SELF_REFERENCE;
        objectSrc = SELF_REFERENCE;
        mediaSrc = SELF_REFERENCE;
        frameSrc = SELF_REFERENCE;
    }

    /**
     * Add CSP policies on each HTTP response.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String contentSecurityPolicyHeaderName = CONTENT_SECURITY_POLICY_HEADER;
            String contentSecurityPolicy = getContentSecurityPolicy();

            httpResponse.addHeader(contentSecurityPolicyHeaderName, contentSecurityPolicy);
        }
        chain.doFilter(request, response);
    }

    private String getContentSecurityPolicy() {
        StringBuilder contentSecurityPolicy = new StringBuilder(DEFAULT_SRC).append(" ").append(defaultSrc);

        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, IMG_SRC, imgSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, SCRIPT_SRC, scriptSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, STYLE_SRC, styleSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, FONT_SRC, fontSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, CONNECT_SRC, connectSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, OBJECT_SRC, objectSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, MEDIA_SRC, mediaSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, FRAME_SRC, frameSrc);
        addDirectiveToContentSecurityPolicy(contentSecurityPolicy, REPORT_URI, reportUri);
        addSandoxDirectiveToContentSecurityPolicy(contentSecurityPolicy, sandboxValue);

        return contentSecurityPolicy.toString();
    }

    private void addDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy, String directiveName, String value) {
        if (isNotBlank(value) && !defaultSrc.equals(value)) {
            contentSecurityPolicy.append("; ").append(directiveName).append(" ").append(value);
        }
    }

    private void addSandoxDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy, String value) {
        if (isNotBlank(value)) {
            if ("true".equalsIgnoreCase(value)) {
                contentSecurityPolicy.append("; ").append(SANDBOX);
            } else {
                contentSecurityPolicy.append("; ").append(SANDBOX).append(" ").append(value);
            }
        }
    }

    private boolean isNotBlank(String s) {
        return s!=null && !s.trim().equals("");
    }

    @Override
    public void destroy() {
        // Not used
    }
}