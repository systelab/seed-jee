package com.systelab.seed.patientallergy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Patient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class PatientAllergy implements Serializable {


    @JsonIgnore
    @EmbeddedId
    private PatientAllergyId id;

    @JsonIgnore
    @ManyToOne
    @MapsId("patientId")
    private Patient patient;

    @ManyToOne
    @MapsId("allergyId")
    private Allergy allergy;

    @Schema(description = "ISO 8601 Format.", example = "1986-01-22T23:28:56.782Z")
    private LocalDate lastOccurrence;
    @Schema(description = "ISO 8601 Format.", example = "1986-01-22T23:28:56.782Z")
    private LocalDate assertedDate;

    @NotNull
    @Size(min = 1, max = 255)
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy) {
        this.id = new PatientAllergyId(patient.getId(), allergy.getId());
        this.patient = patient;
        this.allergy = allergy;
    }
}
