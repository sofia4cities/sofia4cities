/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.config.services.dashboard;

import java.util.List;

import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardUserAccess;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardCreateDTO;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardDTO;

public interface DashboardService {

	List<DashboardDTO> findDashboardWithIdentificationAndDescription(String identification, String description,
			String user);

	List<String> getAllIdentifications();

	void deleteDashboard(String id, String userId);

	void saveDashboard(String id, Dashboard dashboard, String userId);

	Dashboard getDashboardById(String id, String userId);

	String getCredentialsString(String userId);

	String createNewDashboard(DashboardCreateDTO dashboard, String userId);

	boolean hasUserPermission(String id, String userId);

	boolean dashboardExists(String identification);

	void saveDashboardModel(String id, String model, boolean visible, String userId);

	List<DashboardUserAccess> getDashboardUserAccesses(String dashboardId);

	String saveUpdateAccess(DashboardCreateDTO dashboard, String userId);

	String updatePublicDashboard(DashboardCreateDTO dashboard, String userId);

	String cleanDashboardAccess(DashboardCreateDTO dashboard, String userId);

	Dashboard getDashboardEditById(String id, String userId);

	boolean hasUserEditPermission(String id, String userId);

}
