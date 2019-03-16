package com.systelab.seed.patientallergy.entity;

import com.systelab.seed.BaseEntity;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.patient.entity.Patient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class PatientAllergy implements Serializable {


    @EmbeddedId
    private PatientAllergyId id;

    @XmlTransient
    @ManyToOne
    @MapsId("patientId")
    private Patient patient;

    @ManyToOne
    @MapsId("allergyId")
    private Allergy allergy;

    private LocalDate lastOccurrence;
    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy) {
        this.id = new PatientAllergyId(patient.getId(), allergy.getId());
        this.patient = patient;
        this.allergy = allergy;
    }
}
