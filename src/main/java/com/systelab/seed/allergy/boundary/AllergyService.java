package com.systelab.seed.allergy.boundary;

import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;

import javax.ejb.Local;
import java.util.UUID;

@Local
public interface AllergyService {

    Page<Allergy> getAllAllergies(Pageable pageable);


    Allergy getAllergy(UUID patientId);

    void create(Allergy allergy);

    Allergy update(UUID id, Allergy allergy);

    void delete(UUID id) throws AllergyNotFoundException;
}