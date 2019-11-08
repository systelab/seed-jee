package com.systelab.seed.patient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.systelab.seed.BaseEntity;
import com.systelab.seed.infrastructure.constraints.Email;
import com.systelab.seed.infrastructure.jaxb.JsonLocalDateTypeAdapter;
import com.systelab.seed.patientallergy.entity.PatientAllergy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
@XmlType(propOrder = {"id", "creationTime", "updateTime", "name", "surname", "medicalNumber", "email", "dob", "address"})

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient")
@NamedQueries({@NamedQuery(name = Patient.FIND_ALL, query = "SELECT p FROM Patient p ORDER BY p.surname"),
        @NamedQuery(name = Patient.ALL_COUNT, query = "SELECT COUNT(p.id) FROM Patient p"),
        @NamedQuery(name = Patient.DEACTIVATE, query = "UPDATE Patient p SET p.active = FALSE WHERE p.modificationTime < :modificationTime")})
public class Patient extends BaseEntity {
    public static final String FIND_ALL = "Patient.findAll";
    public static final String ALL_COUNT = "Patient.allCount";
    public static final String DEACTIVATE = "Patient.deactivate";

    @NotNull
    @Size(min = 1, max = 255)
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 255)
    private String medicalNumber;

    @Email
    private String email;

    @XmlJavaTypeAdapter(JsonLocalDateTypeAdapter.class)
    @Schema(description = "ISO 8601 Format.", example = "1986-01-22T23:28:56.782Z")
    private LocalDate dob;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER)
    private Set<PatientAllergy> allergies = new HashSet<>();

}