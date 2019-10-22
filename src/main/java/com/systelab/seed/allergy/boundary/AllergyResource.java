package com.systelab.seed.allergy.boundary;

import com.systelab.seed.allergy.entity.AllergiesPage;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.infrastructure.SLF4JLogger;
import com.systelab.seed.infrastructure.auth.AuthenticationTokenNeeded;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;
import org.slf4j.Logger;

@Tag(name = "Allergy")
@Path("allergies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AllergyResource {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String INVALID_ALLERGY_ERROR_MESSAGE = "Invalid Allergy";

    @Inject
    @SLF4JLogger
    private Logger logger;

    @EJB
    private AllergyService allergyService;

    @Operation(description = "Get all Allergies", summary = "Get all Allergies")
    @ApiResponse(responseCode = "200", description = "A Page of Allergies", content = @Content(schema = @Schema(implementation = AllergiesPage.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @PermitAll
    public Response getAllAllergies(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("20") @QueryParam("size") int itemsPerPage) {
        try {
            Page<Allergy> allergies = allergyService.getAllAllergies(new Pageable(page, itemsPerPage));

            return Response.ok().entity(new GenericEntity<Page<Allergy>>(allergies) {
            }).build();
        } catch (Exception ex) {
            logger.error(AllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Create an Allergy", summary = "Create an Allergy")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "An Allergy", content = @Content(schema = @Schema(implementation = Allergy.class)))
    @ApiResponse(responseCode = "400", description = "Validation exception")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @POST
    @Path("allergy")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response createAllergy(@RequestBody(description = "Allergy", required = true, content = @Content(
            schema = @Schema(implementation = Allergy.class))) @Valid Allergy allergy) {
        try {
            allergy.setId(null);
            allergyService.create(allergy);
            return Response.ok().entity(allergy).build();
        } catch (Exception ex) {
            logger.error(AllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Create or Update (idempotent) an existing Allergy", summary = "Create or Update (idempotent) an existing Allergy")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "An Allergy", content = @Content(schema = @Schema(implementation = Allergy.class)))
    @ApiResponse(responseCode = "400", description = "Validation exception")
    @ApiResponse(responseCode = "404", description = "Allergy not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @PUT
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response updateAllergy(@PathParam("uid") String allergyId, @RequestBody(description = "Allergy", required = true, content = @Content(
            schema = @Schema(implementation = Allergy.class))) @Valid Allergy allergy) {
        try {
            UUID id = UUID.fromString(allergyId);
            allergy.setId(id);
            Allergy updatedAllergy = allergyService.update(id, allergy);
            return Response.ok().entity(updatedAllergy).build();
        } catch (Exception ex) {
            logger.error(AllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get Allergy", summary = "Get Allergy")
    @ApiResponse(responseCode = "200", description = "An Allergy", content = @Content(schema = @Schema(implementation = Allergy.class)))
    @ApiResponse(responseCode = "404", description = "Allergy not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @Path("{uid}")
    @PermitAll
    public Response getAllergy(@PathParam("uid") String allergyId) {
        try {
            UUID id = UUID.fromString(allergyId);
            Allergy allergy = allergyService.getAllergy(id);

            if (allergy == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok().entity(allergy).build();
        } catch (Exception ex) {
            logger.error(AllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Delete a Allergy", summary = "Delete an Allergy")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @DELETE
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @RolesAllowed("ADMIN")
    public Response remove(@PathParam("uid") String allergyId) {
        try {
            UUID id = UUID.fromString(allergyId);
            allergyService.delete(id);
            return Response.ok().build();
        } catch (AllergyNotFoundException ex) {
            logger.error(AllergyResource.INVALID_ALLERGY_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.error(AllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
