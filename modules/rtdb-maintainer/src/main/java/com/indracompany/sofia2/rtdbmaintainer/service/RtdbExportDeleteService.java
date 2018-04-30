package com.indracompany.sofia2.rtdbmaintainer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.persistence.services.ManageDBPersistenceServiceFacade;
import com.indracompany.sofia2.rtdbmaintainer.audit.aop.RtdbMaintainerAuditable;

@Service
public class RtdbExportDeleteService {

	@Autowired
	private ManageDBPersistenceServiceFacade manageDBPersistenceServiceFacade;

	@RtdbMaintainerAuditable
	public String performExport(Ontology ontology) {
		long millisecondsQuery = System.currentTimeMillis();
		if (ontology.getRtdbCleanLapse() != null)
			millisecondsQuery = millisecondsQuery - ontology.getRtdbCleanLapse().getMilliseconds();
		return manageDBPersistenceServiceFacade.exportToJson(ontology.getRtdbDatasource(), ontology.getIdentification(),
				millisecondsQuery);
	}

	@RtdbMaintainerAuditable
	public void performDelete(Ontology ontology, String query) {
		this.manageDBPersistenceServiceFacade.deleteAfterExport(ontology.getRtdbDatasource(),
				ontology.getIdentification(), query);

	}
}
