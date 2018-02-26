package com.indracompany.sofia2.config.services.datamodel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.repository.DataModelRepository;

@Service
public class DataModelServiceImpl implements DataModelService{
	
	@Autowired
	private DataModelRepository dataModelRepository;

	@Override
	public void deleteDataModel(String id) {
		dataModelRepository.delete(id);		
	}

	@Override
	public void createDataModel(DataModel dataModel) {
		dataModelRepository.save(dataModel);
	}

	@Override
	public List<DataModel> getAllDataModels() {
		return dataModelRepository.findAll();
	}

	@Override
	public List<DataModel> getAllUsersByCriteria(String datamodelId, String name, String description) {
		return dataModelRepository.findByIdOrNameOrDescription(datamodelId, name, description);
	}

}
