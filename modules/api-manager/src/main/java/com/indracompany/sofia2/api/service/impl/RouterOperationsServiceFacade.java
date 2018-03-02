package com.indracompany.sofia2.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.router.service.app.model.NotificationModel;
import com.indracompany.sofia2.router.service.app.model.OperationResultModel;
import com.indracompany.sofia2.router.service.app.service.RouterCrudService;
import com.indracompany.sofia2.router.service.app.service.RouterService;

@Service
public class RouterOperationsServiceFacade implements RouterService {
	
	@Autowired(required=false)
	private RouterCrudService routerCrudService;
	
	@Autowired(required=false)
	private RouterService routerService;
	
	private boolean checkRouter() {
		if (routerService!=null) return true;
		else return false;
	}

	@Override
	public OperationResultModel insert(NotificationModel model) throws Exception {
		if (checkRouter()) return routerService.insert(model);
		else {
			return routerCrudService.insert(model.getOperationModel());
		}
	}

	@Override
	public OperationResultModel update(NotificationModel model) throws Exception {
		if (checkRouter()) return routerService.update(model);
		else {
			return routerCrudService.update(model.getOperationModel());
		}
	}

	@Override
	public OperationResultModel delete(NotificationModel model) throws Exception {
		if (checkRouter()) return routerService.delete(model);
		else {
			return routerCrudService.delete(model.getOperationModel());
		}
	}

	@Override
	public OperationResultModel query(NotificationModel model) throws Exception {
		if (checkRouter()) return routerService.query(model);
		else {
			return routerCrudService.query(model.getOperationModel());
		}
	}

	@Override
	public OperationResultModel subscribe(NotificationModel model) throws Exception {
		if (checkRouter()) return routerService.subscribe(model);
		else {
			return routerCrudService.subscribe(model.getOperationModel());
		}
	}

}
