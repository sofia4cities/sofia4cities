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
package com.indracompany.sofia2.solver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.indracompany.sofia2.dto.socket.FilterStt;
import com.indracompany.sofia2.dto.socket.ProjectStt;
import com.indracompany.sofia2.persistence.services.QueryToolService;

@Component
@Qualifier("QuasarSolver")
public class SolverQuasarImpl implements SolverInterface{
	
	@Autowired
	QueryToolService qts;
	
	private static String filterSeparator = " and ";
	
	@Override
	public String buildQueryAndSolve(String query, int maxreg , List<FilterStt> where, List<ProjectStt> project, List<String> group) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(buildProject(project));
		sb.append(" from ");
		sb.append("(");
		sb.append(query);
		sb.append(" ) AS Solved ");
		sb.append(buildWhere(where));
		sb.append(buildGroup(group));
		sb.append(" limit "); 
		sb.append(maxreg);
		return qts.querySQLAsJson(SecurityContextHolder.getContext().getAuthentication().getName(), getOntologyFromDatasource(query), sb.toString(), 0);
	}
	
	private String buildProject(List<ProjectStt> projections) {
		if(projections==null || projections.size() == 0) {
			return "* ";
		}
		else {
			StringBuilder sb = new StringBuilder();
			for(ProjectStt p: projections) {
				sb.append(p.getOp());
				sb.append("(");
				sb.append(p.getField());
				sb.append(")");
				sb.append(",");
			}
			return sb.substring(0, sb.length()-1).toString();
		}
	}
	
	private String buildWhere(List<FilterStt> filters) {
		if(filters==null || filters.size() == 0) {
			return "";
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(" where ");
			for(FilterStt f: filters) {
				sb.append("Solved.");
				sb.append(f.getField());
				sb.append(" ");
				sb.append(f.getOp());
				sb.append(" ");
				sb.append(f.getExp());
				sb.append(filterSeparator);
			}
			return sb.substring(0, sb.length()-filterSeparator.length()).toString();
		}
	}
	
	private String buildGroup(List<String> groups) {
		if(groups==null || groups.size() == 0) {
			return "";
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(" group by ");
			for(String g: groups) {
				sb.append(g);
				sb.append(",");
			}
			return sb.substring(0, sb.length()-1).toString();
		}
	}
	
	private String getOntologyFromDatasource(String datasource) {
		int indexfrom = datasource.toLowerCase().indexOf("from ");
		int indexOf = datasource.toLowerCase().indexOf(" ",indexfrom + 5);
		String testOntology = datasource.substring(indexfrom + 5, indexOf).trim();
		while(testOntology.startsWith("(") && indexfrom!=-1) {
			indexfrom = datasource.toLowerCase().indexOf("from ",indexfrom);
			indexOf = datasource.toLowerCase().indexOf(" ",indexfrom + 5);
			testOntology = datasource.substring(indexfrom + 5, indexOf).trim();
		}
		
		if(indexfrom==-1) {
			return "";
		}
		return testOntology;
		
	}
}
