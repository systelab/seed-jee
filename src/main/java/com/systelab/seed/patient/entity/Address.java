package com.systelab.seed.patient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address implements Serializable {

    private String coordinates;
    private String street;
    private String city;
    private String zip;
}
