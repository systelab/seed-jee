package com.systelab.seed.infrastructure.security;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class SecurityResponseHeadersFilter implements ContainerResponseFilter {

  /**
   * Enables a sandbox for the requested resource similar to the iframe sandbox attribute. The
   * sandbox applies a same origin policy, prevents popups, plugins and script execution is blocked.
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
  public static final String REPORT_URI = "report-uri";

  public static final String SELF_REFERENCE = "'self'";
  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
  public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
  public static final String X_XSS_PROTECTION = "X-XSS-Protection";
  public static final String X_FRAME_OPTIONS = "X-Frame-Options";
  public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
  public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";

  /**
   * set of CSP policies that will be applied on each HTTP response. https://www.owasp.org/index.php/Content_Security_Policy
   */
  private static String reportUri = "/ContentSecurityPolicyReporter";
  private static String sandboxValue = "";
  private static String defaultSrc = "none";
  private static String imgSrc = SELF_REFERENCE + " data: online.swagger.io";
  private static String scriptSrc = SELF_REFERENCE + " 'unsafe-inline' ";
  private static String styleSrc = SELF_REFERENCE + " 'unsafe-inline' fonts.googleapis.com";
  private static String fontSrc = SELF_REFERENCE + " fonts.gstatic.com";
  private static String connectSrc = SELF_REFERENCE;
  private static String objectSrc = SELF_REFERENCE;
  private static String mediaSrc = SELF_REFERENCE;
  private static String frameSrc = SELF_REFERENCE;

  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response)
      throws IOException {
    addCORSHeaders(response);
    addXSSHeaders(response);
    addXFrameOptionsHeader(response);
    addContentSecurityPolice(response);
    addHSTSHeader(response);
  }

  /**
   * This servlet filter protects the complete domain by forcing HTTPS usage. The HSTS header
   * doesn't have an effect while the site is accessed over HTTP. When accesses over HTTPS, all the
   * following accesses will be done over HTTPS.
   *
   * HSTS is abbreviated as HTTP Strict Transport Security. HTTP Strict Transport Security (HSTS) is
   * a web security policy mechanism that helps to protect websites against protocol downgrade
   * attacks and cookie hijacking. Once a supported browser receives this header that browser will
   * prevent any communications from being sent over HTTP to the specified domain and will instead
   * send all communications over HTTPS. It also prevents HTTPS click through prompts on browsers.
   */
  private void addHSTSHeader(ContainerResponseContext response) {
    response.getHeaders().add(STRICT_TRANSPORT_SECURITY, "max-age=300; includeSubDomains");
  }


  /**
   * x-frame-options (XFO), is a header that helps to protect your visitors against clickjacking
   * attacks. Is a HTTP response header, also referred to as a HTTP security header. This header
   * tells your browser how to behave when handling your site’s content. The main reason for its
   * inception was to provide clickjacking protection by not allowing rendering of a page in a
   * frame.
   */
  private void addXFrameOptionsHeader(ContainerResponseContext response) {
    response.getHeaders().add(X_FRAME_OPTIONS, "deny");
  }

  /**
   * The HTTP X-XSS-Protection response header is a feature of Internet Explorer, Chrome and Safari
   * that stops pages from loading when they detect reflected cross-site scripting (XSS) attacks.
   * Although these protections are largely unnecessaryin modern browsers when sites implement a
   * strong Content-Security-Policy (CSP) that disables the use of inline JavaScript
   * ('unsafe-inline'),  they can still provide protections for users of older web browsers that
   * don't yet support CSP.
   */
  private void addXSSHeaders(ContainerResponseContext response) {
    response.getHeaders().add(X_XSS_PROTECTION, "1; mode=block");
  }

  /**
   * add security response headers related with CORS (Cross-Origin Resource Sharing)
   * https://www.w3.org/TR/cors/#access-control-allow-headers-response-header
   */
  private void addCORSHeaders(ContainerResponseContext response) {
    response.getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    response.getHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS,
        "origin, content-type, accept, authorization, Etag, if-none-match");
    response.getHeaders().add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    response.getHeaders()
        .add(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    response.getHeaders().add(ACCESS_CONTROL_MAX_AGE, "1209600");
    response.getHeaders().add(ACCESS_CONTROL_EXPOSE_HEADERS,
        "origin, content-type, accept, authorization, ETag, if-none-match");
  }

  private void addContentSecurityPolice(ContainerResponseContext response) {
    StringBuilder contentSecurityPolicy = new StringBuilder(DEFAULT_SRC).append(" ")
        .append(defaultSrc);

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
    response.getHeaders()
        .putSingle(CONTENT_SECURITY_POLICY_HEADER, contentSecurityPolicy.toString());
  }

  private void addDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy,
      String directiveName,
      String value) {
    if (isNotBlank(value) && !defaultSrc.equals(value)) {
      contentSecurityPolicy.append("; ").append(directiveName).append(" ").append(value);
    }
  }

  private void addSandoxDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy,
      String value) {
    if (isNotBlank(value)) {
      if ("true".equalsIgnoreCase(value)) {
        contentSecurityPolicy.append("; ").append(SANDBOX);
      } else {
        contentSecurityPolicy.append("; ").append(SANDBOX).append(" ").append(value);
      }
    }
  }

  private boolean isNotBlank(String s) {
    return s != null && !s.trim().equals("");
  }

}
