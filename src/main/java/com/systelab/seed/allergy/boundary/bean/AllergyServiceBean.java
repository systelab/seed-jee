package com.systelab.seed.allergy.boundary.bean;

import com.systelab.seed.allergy.boundary.AllergyNotFoundException;
import com.systelab.seed.allergy.boundary.AllergyService;
import com.systelab.seed.allergy.entity.Allergy;
import com.systelab.seed.infrastructure.pagination.Page;
import com.systelab.seed.infrastructure.pagination.Pageable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Stateless
public class AllergyServiceBean implements AllergyService {

    @PersistenceContext(unitName = "SEED")
    private EntityManager em;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Allergy allergy) {
        em.persist(allergy);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Allergy update(UUID id, Allergy allergy) {
        em.merge(allergy);
        return allergy;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(UUID id) throws AllergyNotFoundException {
        Allergy p = em.find(Allergy.class, id);
        if (p != null) {
            em.remove(p);
        } else {
            throw new AllergyNotFoundException();
        }
    }

    @Override
    public Page<Allergy> getAllAllergies(Pageable pageable) {

        TypedQuery<Long> queryTotal = em.createNamedQuery(Allergy.ALL_COUNT, Long.class);
        TypedQuery<Allergy> query = em.createNamedQuery(Allergy.FIND_ALL, Allergy.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Allergy> allergies = query.getResultList();
        long totalElements = queryTotal.getSingleResult();

        return new Page<>(allergies, totalElements);
    }

    @Override
    public Allergy getAllergy(UUID allergyId) {
        return em.find(Allergy.class, allergyId);
    }

}
