/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.indracompany.sofia2.config.model.GadgetDataModel;

public interface GadgetDataModelRepository extends JpaRepository<GadgetDataModel,String> {
	
	List<GadgetDataModel> findByIdentification(String identification);
	List<GadgetDataModel> findByUserIdOrIsPublicTrue(String userId);
	@Query("SELECT o FROM GadgetDataModel AS o WHERE o.identification = :#{#identification} and (o.userId = :#{#userId} OR o.isPublic=true)")
	List<GadgetDataModel> findByIdentificationAndUserIdOrIsPublicTrue(@Param("identification") String identification,@Param("userId")String userId);
	
	
	
}
