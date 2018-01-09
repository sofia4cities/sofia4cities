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

public interface GadgetRepository extends JpaRepository<Gadget,String>{

	List<Gadget> findByUserId(String userId);
	List<Gadget> findByUserIdAndType(String userId, String type);
	List<Gadget> findByNameLike(String name);
	List<Gadget> findByType(String type);



}
