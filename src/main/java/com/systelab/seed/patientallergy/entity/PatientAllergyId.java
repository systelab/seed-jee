package com.systelab.seed.patientallergy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientAllergyId implements Serializable {

    @Column(name = "fk_patient")
    protected UUID patientId;

    @Column(name = "fk_allergy")
    protected UUID allergyId;
}
