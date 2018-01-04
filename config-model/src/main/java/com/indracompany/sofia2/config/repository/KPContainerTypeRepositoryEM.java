/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.config.model.KPContainerType;

@Repository
public class KPContainerTypeRepositoryEM  {

    @PersistenceContext
    protected EntityManager entityManager; 
    
	public long count() {
        return entityManager.createQuery("SELECT COUNT(o) FROM KPCONTAINER_TYPE o", Long.class).getSingleResult();
    }
    
	public List<KPContainerType> findAll() {
        return entityManager.createQuery("SELECT o FROM KPCONTAINER_TYPE o", KPContainerType.class).getResultList();
    }

	public KPContainerType findById(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager.find(KPContainerType.class, id);
    }

	public List<KPContainerType> findPage(int firstResult, int maxResults) {
        return entityManager.createQuery("SELECT o FROM KPCONTAINER_TYPE o", KPContainerType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

}
