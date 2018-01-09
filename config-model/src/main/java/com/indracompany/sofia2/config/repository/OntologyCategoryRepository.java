/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.indracompany.sofia2.config.model.OntologyCategory;

public interface OntologyCategoryRepository extends JpaRepository<OntologyCategory, Integer>{

	List<OntologyCategory> findById(Integer id);
}
