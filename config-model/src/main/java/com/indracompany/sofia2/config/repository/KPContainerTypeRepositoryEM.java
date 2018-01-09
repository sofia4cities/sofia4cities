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

import com.indracompany.sofia2.config.model.ClientPlatformContainerType;

@Repository
public class KPContainerTypeRepositoryEM  {

	@PersistenceContext
	protected EntityManager entityManager; 

	public long count() {
		return entityManager.createQuery("SELECT COUNT(o) FROM KPCONTAINER_TYPE o", Long.class).getSingleResult();
	}

	public List<ClientPlatformContainerType> findAll() {
		return entityManager.createQuery("SELECT o FROM KPCONTAINER_TYPE o", ClientPlatformContainerType.class).getResultList();
	}

	public ClientPlatformContainerType findById(String id) {
		if (id == null || id.length() == 0) return null;
		return entityManager.find(ClientPlatformContainerType.class, id);
	}

	public List<ClientPlatformContainerType> findPage(int firstResult, int maxResults) {
		return entityManager.createQuery("SELECT o FROM KPCONTAINER_TYPE o", ClientPlatformContainerType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
	}

}
