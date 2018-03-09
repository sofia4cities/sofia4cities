/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.GadgetDatasource;
import com.indracompany.sofia2.config.repository.GadgetDatasourceRepository;
import com.indracompany.sofia2.dto.socket.InputMessage;
import com.indracompany.sofia2.security.AppWebUtils;
import com.indracompany.sofia2.solver.SolverInterface;
import com.indracompany.sofia2.solver.SolverQuasarImpl;

@Service
public class SolverServiceImpl implements SolverService{
	
	@Autowired
	GadgetDatasourceRepository gdr;
	
	@Autowired
	AppWebUtils utils;
	
	@Autowired
	@Qualifier("QuasarSolver")
	SolverInterface quasarSolver;
	
	@Override
	public String solveDatasource(InputMessage im) {
		GadgetDatasource gd = gdr.findByIdentification(im.getDs());
		
		if(gd==null) {
			return "Not found: 404 for user " + utils.getUserId() + " datasource: " + im.getDs();
		}
		
		if(!gd.getUser().getUserId().equals(utils.getUserId()) && utils.isAdministrator()) {
			return "Permision denied: 403 for user " + utils.getUserId() + " datasource: " + im.getDs();
		}
		
		
		switch(gd.getMode()) {
			case "query":
				return quasarSolver.buildQueryAndSolve(gd.getQuery(), gd.getMaxvalues(), im.getFilter(), im.getProject(), im.getGroup());
			//More types of solver...
		}
		return null;
	}
}
