package com.systelab.seed.model.patient;

import com.systelab.seed.util.constraints.Email;
import com.systelab.seed.util.convert.jaxb.JsonLocalDateTypeAdapter;
import io.swagger.annotations.ApiModelProperty;

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
@Table(name = "patient")
@NamedQueries({@NamedQuery(name = Patient.FIND_ALL, query = "SELECT p FROM Patient p")})
public class Patient implements Serializable {
    public static final String FIND_ALL = "Patient.findAll";

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1, max = 255)
    private String surname;

    @Size(min = 1, max = 255)
    private String name;

    @Email
    private String email;

    @XmlJavaTypeAdapter(JsonLocalDateTypeAdapter.class)
    @ApiModelProperty(value = "ISO 8601 Format.", example = "1986-01-22T23:28:56.782Z")
    private LocalDate dob;

    @Embedded
    private Address address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Patient other = (Patient) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (id != null)
            return surname + ", " + name + " (#" + id + ")";
        else
            return surname + ", " + name;
    }

}