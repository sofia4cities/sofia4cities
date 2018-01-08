/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.indracompany.sofia2.config.model.DashboardType;

public interface DashboardTypeRepository extends JpaRepository<DashboardType, Integer>{
	
	List<DashboardType> findByUserId(String userId);
	List<DashboardType> findByType(String type);
	List<DashboardType> findById(Integer id);
	long countByType(String type);

}
