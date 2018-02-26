package com.indracompany.sofia2.config.services.datamodel;

import java.util.List;

import com.indracompany.sofia2.config.model.DataModel;

public interface DataModelService {

	void deleteDataModel(String id);
	
	void createDataModel(DataModel dataModel);

	List<DataModel> getAllDataModels();

	List<DataModel> getAllUsersByCriteria(String datamodelId, String name, String description);

}
