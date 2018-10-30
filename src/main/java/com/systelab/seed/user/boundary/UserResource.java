package com.systelab.seed.user.boundary;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenNeeded;
import com.systelab.seed.user.entity.UsersPage;
import com.systelab.seed.user.entity.User;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;


@Tag(name = "User")

@Path("users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class UserResource {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    private Logger logger;

    @Context
    private UriInfo uriInfo;

    @EJB
    private UserService userService;

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Operation(description = "User Login", summary = "User Login")
    @ApiResponse(responseCode = "200", description = "An authorization key in the header")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @PermitAll
    public Response authenticateUser(@FormParam("login") String login, @FormParam("password") String password) {
        try {
            String token = userService.getToken(uriInfo.getAbsolutePath().toString(), login, password);

            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "Security Exception", ex);
            return Response.status(UNAUTHORIZED).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Create a User", summary = "Create a User")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(description = "A User", responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Validation exception")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @POST
    @Path("user")
    @AuthenticationTokenNeeded
    @RolesAllowed("ADMIN")
    public Response create(@RequestBody(description = "User", required = true, content = @Content(
            schema = @Schema(implementation = User.class))) @Valid User user) {
        try {
            user.setId(null);
            userService.create(user);
            return Response.ok().entity(user).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get User", summary = "Get User")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "A User", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response findById(@PathParam("uid") String userId) {
        try {
            UUID id=UUID.fromString(userId);
            User user = userService.getUser(id);

            if (user == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok().entity(user).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get all Users", summary = "Get all Users")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "A Page of Users", content = @Content(schema = @Schema(implementation = UsersPage.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @AuthenticationTokenNeeded
    @RolesAllowed("ADMIN")
    public Response getAllUsers(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("20") @QueryParam("size") int itemsPerPage) {
        try {
            Page<User> users = userService.getAllUsers(new Pageable(page, itemsPerPage));
            return Response.ok().entity(new GenericEntity<Page<User>>(users) {
            }).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Delete a User", summary = "Delete a User")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @DELETE
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @RolesAllowed("ADMIN")
    public Response remove(@PathParam("uid") String userId) {
        try {
            UUID id=UUID.fromString(userId);
            userService.delete(id);
            return Response.ok().build();
        } catch (UserNotFoundException ex) {
            logger.log(Level.SEVERE, "Invalid User", ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}