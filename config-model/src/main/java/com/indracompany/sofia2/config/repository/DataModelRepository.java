/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.indracompany.sofia2.config.model.DataModel;
import java.util.List;




public interface DataModelRepository extends JpaRepository<DataModel, String> {
	
	List<DataModel> findByTypeAndIdentificationAndDescription(String type, String identification, String description);
	List<DataModel> findByIdentification(String identification);
	List<DataModel> findByIsrelationalTrue();
	List<DataModel> findByIsrelationalFalse();
	List<DataModel> findByType(String type);
	List<DataModel> findByCategory(String category);
	long countByIdentification(String identification);
	long countByType(String type);
}
