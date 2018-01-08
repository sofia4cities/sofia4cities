/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetQuery;

public interface GadgetQueryRepository extends JpaRepository<GadgetQuery,String>{
	
	List<GadgetQuery> findByGadgetId(Gadget gadgetId);
	List<GadgetQuery> findById(String id);
	

}
