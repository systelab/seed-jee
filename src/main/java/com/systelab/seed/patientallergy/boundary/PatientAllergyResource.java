package com.systelab.seed.patientallergy.boundary;

import com.systelab.seed.allergy.boundary.AllergyNotFoundException;
import com.systelab.seed.patient.boundary.PatientNotFoundException;
import com.systelab.seed.patientallergy.entity.PatientAllergy;
import com.systelab.seed.patientallergy.entity.PatientAllergySet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Tag(name = "Patient")
@Path("patients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PatientAllergyResource {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String INVALID_PATIENT_ERROR_MESSAGE = "Invalid Patient";
    private static final String INVALID_ALLERGY_ERROR_MESSAGE = "Invalid Allergy";

    private Logger logger;

    @EJB
    private PatientAllergyService patientAllergyService;

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Operation(description = "Get Patient allergies", summary = "Get Patient allergies")
    @ApiResponse(responseCode = "200", description = "A Set of Allergies", content = @Content(schema = @Schema(implementation = PatientAllergySet.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @GET
    @Path("{uid}/allergies")
    @PermitAll
    public Response getPatientAllergies(@PathParam("uid") String uid) {
        try {
            Set<PatientAllergy> allergies = patientAllergyService.getPatientAllergies(UUID.fromString(uid));
            return Response.ok().entity(allergies).build();
        } catch (PatientNotFoundException ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INVALID_PATIENT_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Add allergy to patient", summary = "Add allergy to patient")
    @ApiResponse(responseCode = "200", description = "A Patient Allergy", content = @Content(schema = @Schema(implementation = PatientAllergy.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @POST
    @Path("{uid}/allergies/allergy")
    @PermitAll
    public Response addAllergyToPatient(@PathParam("uid") String uid, @RequestBody(description = "Allergy", required = true, content = @Content(
            schema = @Schema(implementation = PatientAllergy.class))) @Valid PatientAllergy patientAllergy) {
        try {
            PatientAllergy savedPatientAllergy = patientAllergyService.addPatientAllergy(UUID.fromString(uid), patientAllergy);
            return Response.ok().entity(savedPatientAllergy).build();
        } catch (PatientNotFoundException ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INVALID_PATIENT_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (AllergyNotFoundException ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INVALID_ALLERGY_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(description = "Remove allergy from patient", summary = "Remove allergy from patient")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @DELETE
    @Path("{patientId}/allergies/{allergyId}")
    @PermitAll
    public Response deleteAllergyFromPatient(@PathParam("patientId") String patientId, @PathParam("allergyId") String allergyId) {
        try {
            patientAllergyService.removePatientAllergy(UUID.fromString(patientId), UUID.fromString(allergyId));
            return Response.ok().build();
        } catch (PatientNotFoundException ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INVALID_PATIENT_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (AllergyNotFoundException ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INVALID_ALLERGY_ERROR_MESSAGE, ex);
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, PatientAllergyResource.INTERNAL_SERVER_ERROR_MESSAGE, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
