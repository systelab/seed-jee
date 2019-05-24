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
  private static final String SANDBOX = "sandbox";
  // The default policy for loading content such as JavaScript, Images, CSS, Font's, AJAX requests, Frames, HTML5 Media
  private static final String DEFAULT_SRC = "default-src";
  // Defines valid sources of images
  private static final String IMG_SRC = "img-src";
  // Defines valid sources of JavaScript
  private static final String SCRIPT_SRC = "script-src";
  // Defines valid sources of stylesheets
  private static final String STYLE_SRC = "style-src";
  // Defines valid sources of fonts
  private static final String FONT_SRC = "font-src";
  // Applies to XMLHttpRequest (AJAX), WebSocket or EventSource
  private static final String CONNECT_SRC = "connect-src";
  // Defines valid sources of plugins, eg <object>, <embed> or <applet>.
  private static final String OBJECT_SRC = "object-src";
  // Defines valid sources of audio and video, eg HTML5 <audio>, <video> elements
  private static final String MEDIA_SRC = "media-src";
  // Defines valid sources for loading frames
  private static final String FRAME_SRC = "frame-src";
  private static final String REPORT_URI = "report-uri";


  private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
  private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
  private static final String X_XSS_PROTECTION = "X-XSS-Protection";
  private static final String X_FRAME_OPTIONS = "X-Frame-Options";
  private static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
  private static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";
  private static final String TRUE = "true";
  private static final String SELF_VALUE = "'self'";
  public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";


  @Override
  public void filter(ContainerRequestContext request, ContainerResponseContext response)
      throws IOException {
    addCORSHeaders(response);
    addXSSHeaders(response);
    addXFrameOptionsHeader(response);
    addContentSecurityPolice(response);
    addHSTSHeader(response);
    adContentTypeOptionsHeaders(response);


  }


  /**
   This header prevents "mime" based attacks. This header prevents Internet Explorer from
   MIME-sniffing a response away from the declared content-type as the header instructs the
   browser not to override the response content type. With the nosniff option, if the server
   says the content is text/html, the browser will render it as text/html.

   https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
   **/
  private void adContentTypeOptionsHeaders(ContainerResponseContext response) {
    response.getHeaders().add(X_CONTENT_TYPE_OPTIONS, "nosniff");
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
        .append("none");
    /**
     * set of CSP policies that will be applied on each HTTP response. https://www.owasp.org/index.php/Content_Security_Policy
     */

    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, IMG_SRC,
        SELF_VALUE + " data: online.swagger.io");
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, SCRIPT_SRC,
        SELF_VALUE + " 'unsafe-inline' ");
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, STYLE_SRC,
        SELF_VALUE + " 'unsafe-inline' fonts.googleapis.com");
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, FONT_SRC,
        SELF_VALUE + " fonts.gstatic.com");
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, CONNECT_SRC, SELF_VALUE);
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, OBJECT_SRC, SELF_VALUE);
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, MEDIA_SRC, SELF_VALUE);
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, FRAME_SRC, SELF_VALUE);
    addDirectiveToContentSecurityPolicy(contentSecurityPolicy, REPORT_URI,
        "/ContentSecurityPolicyReporter");
    addSandoxDirectiveToContentSecurityPolicy(contentSecurityPolicy, "");
    response.getHeaders()
        .putSingle(CONTENT_SECURITY_POLICY_HEADER, contentSecurityPolicy.toString());
  }

  private void addDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy,
      String directiveName,
      String value) {
    if (isNotBlank(value) && !"none".equals(value)) {
      contentSecurityPolicy.append("; ").append(directiveName).append(" ").append(value);
    }
  }

  private void addSandoxDirectiveToContentSecurityPolicy(StringBuilder contentSecurityPolicy,
      String value) {
    if (isNotBlank(value)) {
      if (TRUE.equalsIgnoreCase(value)) {
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
