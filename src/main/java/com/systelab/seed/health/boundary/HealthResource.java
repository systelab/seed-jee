package com.systelab.seed.health.boundary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Tag(name = "Health")

@Path("health")
@Produces({MediaType.TEXT_PLAIN})
public class HealthResource {


    @Operation(summary = "Check Health", description = "Check Health")
    @ApiResponse(responseCode = "200", description = "Healthy!")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @PermitAll
    public Response checkHealth() {
        try {
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}