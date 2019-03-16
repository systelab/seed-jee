package com.systelab.seed.allergy.entity;

import com.systelab.seed.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allergy")
@NamedQueries({@NamedQuery(name = com.systelab.seed.allergy.entity.Allergy.FIND_ALL, query = "SELECT a FROM Allergy a ORDER BY a.name"),
        @NamedQuery(name = com.systelab.seed.allergy.entity.Allergy.ALL_COUNT, query = "SELECT COUNT(a.id) FROM Allergy a")})
public class Allergy extends BaseEntity implements Serializable {
    public static final String FIND_ALL = "Allergy.findAll";
    public static final String ALL_COUNT = "Allergy.allCount";

    @NotNull
    @Size(min = 1, max = 255)
    public String name;

    @NotNull
    @Size(min = 1, max = 255)
    public String signs;

    @Size(min = 1, max = 255)
    public String symptoms;
}