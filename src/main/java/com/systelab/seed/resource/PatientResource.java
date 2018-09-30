package com.systelab.seed.resource;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenNeeded;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientsPage;
import com.systelab.seed.service.MedicalRecordNumberService;
import com.systelab.seed.service.PatientService;
import com.systelab.seed.util.exceptions.PatientNotFoundException;
import com.systelab.seed.util.pagination.Page;
import com.systelab.seed.util.pagination.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Tag(name = "Patient")
@Path("patients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PatientResource {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String INVALID_PATIENT_ERROR_MESSAGE = "Invalid Patient";

    private Logger logger;

    @Inject
    private MedicalRecordNumberService medicalRecordNumberService;

    @EJB
    private PatientService patientService;

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Operation(description = "Get all Patients", summary = "Get all Patients")
    @ApiResponse(responseCode = "200", description = "A Page of Patients", content = @Content(schema = @Schema(implementation = PatientsPage.class)))
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @PermitAll
    public Response getAllPatients(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("20") @QueryParam("size") int itemsPerPage) {
        try {
            Page<Patient> patients = patientService.getAllPatients(new Pageable(page, itemsPerPage));

            return Response.ok().entity(new GenericEntity<Page<Patient>>(patients) {
            }).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Create a Patient", summary = "Create a Patient")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "A Patient", content = @Content(schema = @Schema(implementation = Patient.class)))
    @ApiResponse(responseCode = "400", description = "Validation exception")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @POST
    @Path("patient")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response createPatient(@RequestBody(description = "Patient", required = true, content = @Content(
            schema = @Schema(implementation = Patient.class))) @Valid Patient patient) {
        try {
            patient.setId(null);
            if (patient.getMedicalNumber() == null || patient.getMedicalNumber().equals("")) {
                patient.setMedicalNumber(medicalRecordNumberService.getMedicalRecordNumber());
            }
            patientService.create(patient);
            return Response.ok().entity(patient).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Create or Update (idempotent) an existing Patient", summary = "Create or Update (idempotent) an existing Patient")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "A Patient", content = @Content(schema = @Schema(implementation = Patient.class)))
    @ApiResponse(responseCode = "400", description = "Validation exception")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @PUT
    @Path("{uid}")
    @AuthenticationTokenNeeded
    @PermitAll
    public Response updatePatient(@PathParam("uid") Long patientId, @RequestBody(description = "Patient", required = true, content = @Content(
            schema = @Schema(implementation = Patient.class))) @Valid Patient patient) {
        try {
            patient.setId(patientId);
            Patient updatedPatient = patientService.update(patientId, patient);
            return Response.ok().entity(updatedPatient).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Get Patient", summary = "Get Patient")
    @ApiResponse(responseCode = "200", description = "A Patient", content = @Content(schema = @Schema(implementation = Patient.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
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

    @Operation(description = "Get all Patients in an Excel file", summary = "Get all Patients in an Excel file")
    @ApiResponse(responseCode = "200", description = "An Excel file")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @Path("/report")
    @PermitAll
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAllPatientsInExcel() {

        String fileName = "patients.xlsx";
        try (final XSSFWorkbook wb = patientService.getPatientsWorkbook()) {
            StreamingOutput stream = (OutputStream output) -> wb.write(output);
            return Response.ok(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("content-disposition", "attachment; filename=" + fileName).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Delete a Patient", summary = "Delete a Patient")
    @SecurityRequirement(name = "Authorization")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
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
