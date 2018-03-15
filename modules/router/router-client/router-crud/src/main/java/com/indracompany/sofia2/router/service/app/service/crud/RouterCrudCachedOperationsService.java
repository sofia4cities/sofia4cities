package com.indracompany.sofia2.router.service.app.service.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.router.service.app.model.OperationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@CacheConfig(cacheNames={"queries"})
public class RouterCrudCachedOperationsService {
	
	@Autowired
	RouterCrudServiceImpl routerCrudServiceImpl;
	
	@Cacheable("queries")
	public OperationResultModel queryCache(OperationModel operationModel) {
		
		log.info("Router CACHE EXPLICIT Crud Service Operation "+operationModel.toString());
		OperationResultModel result = routerCrudServiceImpl.queryNoCache(operationModel);
		return result;

	}
}
