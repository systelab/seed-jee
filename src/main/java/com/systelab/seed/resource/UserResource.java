package com.systelab.seed.resource;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenNeeded;
import com.systelab.seed.model.user.User;
import com.systelab.seed.service.UserService;
import com.systelab.seed.util.exceptions.UserNotFoundException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Api(value = "User")

@Path("users")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class UserResource
{
  public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

  private Logger logger;

  @Context
  private UriInfo uriInfo;

  @EJB
  private UserService userService;

  @Inject
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }

  @ApiOperation(value = "User Login", notes = "")
  @ApiResponses(value = { @ApiResponse(code = 200, message = "An authorization key in the header"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 500, message = "Internal Server Error") })

  @POST
  @Path("/login")
  @Consumes(APPLICATION_FORM_URLENCODED)
  @PermitAll
  public Response authenticateUser(@FormParam("login") String login, @FormParam("password") String password)
  {
    try
    {
      String token = userService.getToken(uriInfo.getAbsolutePath().toString(), login, password);

      return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
    }
    catch (SecurityException ex)
    {
      logger.log(Level.SEVERE, "Security Exception", ex);
      return Response.status(UNAUTHORIZED).build();
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @ApiOperation(value = "Create a User", notes = "", authorizations = { @Authorization(value = "Bearer") })
  @ApiResponses(value = { @ApiResponse(code = 200, message = "A User", response = User.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 500, message = "Internal Server Error") })

  @POST
  @Path("user")
  @AuthenticationTokenNeeded
  @RolesAllowed("ADMIN")
  public Response create(@ApiParam(value = "User", required = true) @Valid User user)
  {
    try
    {
      user.setId(null);
      userService.create(user);
      return Response.ok().entity(user).build();
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  @ApiOperation(value = "Get User", notes = "", authorizations = { @Authorization(value = "Bearer") })
  @ApiResponses(value = { @ApiResponse(code = 200, message = "A user", response = User.class), @ApiResponse(code = 404, message = "User not found"), @ApiResponse(code = 500, message = "Internal Server Error") })

  @GET
  @Path("{uid}")
  @AuthenticationTokenNeeded
  @PermitAll
  public Response findById(@PathParam("uid") Long userId)
  {
    try
    {
      User user = userService.getUser(userId);

      if (user == null)
      {
        return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok().entity(user).build();
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @ApiOperation(value = "Get all Users", notes = "", authorizations = { @Authorization(value = "Bearer") })
  @ApiResponses(value = { @ApiResponse(code = 200, message = "An array of Users", response = User.class, responseContainer = "List"), @ApiResponse(code = 500, message = "Internal Server Error") })

  @GET
  @AuthenticationTokenNeeded
  @RolesAllowed("ADMIN")
  public Response getAllUsers()
  {
    try
    {
      List<User> users = userService.getAllUsers();
      return Response.ok().entity(new GenericEntity<List<User>>(users)
      {
      }).build();
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  @ApiOperation(value = "Delete a User", notes = "", authorizations = { @Authorization(value = "Bearer") })
  @ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Internal Server Error") })

  @DELETE
  @Path("{uid}")
  @AuthenticationTokenNeeded
  @RolesAllowed("ADMIN")
  public Response remove(@PathParam("uid") Long userId)
  {
    try
    {
      userService.delete(userId);
      return Response.ok().build();
    }
    catch (UserNotFoundException ex)
    {
      logger.log(Level.SEVERE, "Invalid User", ex);
      return Response.status(Status.NOT_FOUND).build();
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, UserResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

  }

}