package com.systelab.seed.resource;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;

@SwaggerDefinition(
    info = @Info(
        description = "Restful API to manage the Seed Application to be used as an example.",
        version = "V1.0",
        title = "Seed application programming interface (API)",
        contact = @Contact(name = "Systelab Technologies", email = "support@systelabsw.com", url = "http://www.systelabsw.com")),
    consumes = { "application/json" },
    produces = { "application/json" },
    schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS },
    tags = { @Tag(name = "Patient", description = "Patient Management"),
    			@Tag(name = "User", description = "User Management")})

public class SwaggerApiDefinition implements ReaderListener
{

  @Override
  public void beforeScan(Reader reader, Swagger swagger)
  {
    // Not used
  }

  @Override
  public void afterScan(Reader reader, Swagger swagger)
  {

    ApiKeyAuthDefinition tokenScheme = new ApiKeyAuthDefinition();
    tokenScheme.setType("basic");
    tokenScheme.setName("HTTP Basic Authentication");
    swagger.addSecurityDefinition("basicAuth", tokenScheme);

    ApiKeyAuthDefinition tokenSchemeJWT = new ApiKeyAuthDefinition();
    tokenSchemeJWT.setIn(In.HEADER);
    tokenSchemeJWT.setName("Authorization");
    tokenSchemeJWT.setType("apiKey");
    swagger.addSecurityDefinition("Bearer", tokenSchemeJWT);

  }
}
