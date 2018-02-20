package com.indracompany.sofia2.service.gadget;

import java.util.List;

import com.indracompany.sofia2.config.model.GadgetDatasource;

public interface GadgetDatasourceService {

	public List<GadgetDatasource> findAllDatasources();
	public List<GadgetDatasource> findGadgetDatasourceWithIdentificationAndDescription(String identification, String description, String user);
	public List<String> getAllIdentifications();
	public GadgetDatasource getGadgetDatasourceById(String id);
	
}
