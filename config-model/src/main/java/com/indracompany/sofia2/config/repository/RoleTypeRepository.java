/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indracompany.sofia2.config.model.RoleType;

public interface RoleTypeRepository extends JpaRepository<RoleType, Integer> {

	long countByName(String name);
	List<RoleType> findByName(String name);
		
}
