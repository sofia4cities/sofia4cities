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
import com.indracompany.sofia2.config.model.GadgetMeasure;

public interface GadgetMeasureRepository extends JpaRepository<GadgetMeasure,String>{
	
	List<GadgetMeasure> findByGadgetId(Gadget gadgetId);
	List<GadgetMeasure> findById(String id);

}
