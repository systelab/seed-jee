package com.systelab.seed.model.patient;

import com.systelab.seed.util.constraints.Email;
import com.systelab.seed.util.convert.jaxb.JsonLocalDateTypeAdapter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement
@XmlType(propOrder = {"id", "name", "surname", "email", "dob", "address"})

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient")
@NamedQueries({@NamedQuery(name = Patient.FIND_ALL, query = "SELECT p FROM Patient p"),
        @NamedQuery(name = Patient.ALL_COUNT, query = "SELECT COUNT(p.id) FROM Patient p")})
public class Patient implements Serializable {
    public static final String FIND_ALL = "Patient.findAll";
    public static final String ALL_COUNT = "Patient.allCount";

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1, max = 255)
    private String surname;

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
}