package com.systelab.seed.resource;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenNeeded;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.service.PatientService;
import com.systelab.seed.util.exceptions.PatientNotFoundException;
import io.swagger.annotations.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Api(value = "Patient")

@Path("patients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PatientResource {
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public static final String INVALID_PATIENT_ERROR_MESSAGE = "Invalid Patient";

    private Logger logger;

    @EJB
    private PatientService patientService;

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @ApiOperation(value = "Get all Patients", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An array of Patient", response = Patient.class, responseContainer = "List"), @ApiResponse(code = 500, message = "Internal Server Error")})

    @GET
    @PermitAll
    public Response getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            return Response.ok().entity(new GenericEntity<List<Patient>>(patients) {
            }).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 500, message = "Internal Server Error")})

    @POST
    @Path("patient")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response createPatient(@ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        try {
            patient.setId(null);
            patientService.create(patient);
            return Response.ok().entity(patient).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 404, message = "Patient not found"),
                    @ApiResponse(code = 500, message = "Internal Server Error")})

    @PUT
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response updatePatient(@PathParam("uid") Long patientId, @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        try {
            patient.setId(patientId);
            Patient updatedPatient = patientService.update(patientId, patient);
            return Response.ok().entity(updatedPatient).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Get Patient", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 404, message = "Patient not found"), @ApiResponse(code = 500, message = "Internal Server Error")})

    @GET
    @Path("{uid}")
    @PermitAll
    public Response getPatient(@PathParam("uid") Long patientId) {
        try {
            Patient patient = patientService.getPatient(patientId);

            if (patient == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return Response.ok().entity(patient).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "Get Patients as Excel file", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An Excel file", response = File.class), @ApiResponse(code = 500, message = "Internal Server Error")})


    @GET
    @Path("/report")
    @PermitAll
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPatientsExcel() {

        String fileName = "patients.xlsx";
        final XSSFWorkbook wb = patientService.getPatientsWorkbook();

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException {
                try {
                    wb.write(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return Response.ok(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("content-disposition", "attachment; filename=" + fileName).build();

    }

    @ApiOperation(value = "Delete a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Internal Server Error")})

    @DELETE
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @RolesAllowed("ADMIN")
    public Response remove(@PathParam("uid") Long patientId) {
        try {
            patientService.delete(patientId);
            return Response.ok().build();
        } catch (PatientNotFoundException ex) {
            logger.log(Level.SEVERE, PatientResource.INVALID_PATIENT_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

}
