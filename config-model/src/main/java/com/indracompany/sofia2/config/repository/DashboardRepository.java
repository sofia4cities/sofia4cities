/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.Dashboard;

public interface DashboardRepository extends JpaRepository<Dashboard, String>{
	
	List<Dashboard> findById(String id);
	List<Dashboard> findByUserId(String userId);
	List<Dashboard> findByName(String name);
	List<Dashboard> findByDashboardTypeId(String dashboardTypeId);
	//Se omite un método relacionado con gruposusuarios y gruposdashboards
	List<Dashboard> findByNameAndDashboardTypeId(String name, String dashboardTypeId);
	long countByName(String name);
	long countByUserId(String userId);
	

}
