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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.exceptions.GadgetDatasourceServiceException;
import com.indracompany.sofia2.config.services.exceptions.UserServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;
	@Autowired
	private UserRepository userRepository;

	private static final String initialModel = "{\"header\":{\"title\":\"My new s4c Dashboard\",\"enable\":true,\"height\":56,\"logo\":{\"height\":48},\"backgroundColor\":\"hsl(220, 23%, 20%)\",\"textColor\":\"hsl(0, 0%, 100%)\",\"iconColor\":\"hsl(220, 23%, 20%)\",\"pageColor\":\"hsl(0, 0%, 100%)\"},\"navigation\":{\"showBreadcrumbIcon\":true,\"showBreadcrumb\":true},\"pages\":[{\"title\":\"New Page\",\"icon\":\"apps\",\"background\":{\"file\":[]},\"layers\":[{\"gridboard\":[{\"cols\":7,\"rows\":7,\"y\":1,\"x\":2,\"id\":\"1\",\"type\":\"livehtml\",\"content\":\"<h1> LiveHTML Text </h1>\",\"header\":{\"enable\":true,\"title\":{\"icon\":\"archive\",\"iconColor\":\"hsl(220, 23%, 20%)\",\"text\":\"LiveHTML G\",\"textColor\":\"hsl(220, 23%, 20%)\"},\"backgroundColor\":\"hsl(0, 0%, 100%)\",\"height\":25},\"backgroundColor\":\"hsla(0, 0%, 100%, 0.69)\",\"padding\":-19,\"border\":{\"color\":\"hsl(0, 0%, 82%)\",\"width\":1,\"radius\":0},\"$$hashKey\":\"object:68\"}],\"title\":\"baseLayer\",\"$$hashKey\":\"object:23\"}],\"selectedlayer\":0,\"combinelayers\":false,\"$$hashKey\":\"object:4\"}],\"gridOptions\":{\"gridType\":\"fit\",\"compactType\":\"none\",\"margin\":3,\"outerMargin\":true,\"mobileBreakpoint\":640,\"minCols\":20,\"maxCols\":100,\"minRows\":20,\"maxRows\":100,\"maxItemCols\":5000,\"minItemCols\":1,\"maxItemRows\":5000,\"minItemRows\":1,\"maxItemArea\":25000,\"minItemArea\":1,\"defaultItemCols\":4,\"defaultItemRows\":4,\"fixedColWidth\":250,\"fixedRowHeight\":250,\"enableEmptyCellClick\":false,\"enableEmptyCellContextMenu\":false,\"enableEmptyCellDrop\":true,\"enableEmptyCellDrag\":false,\"emptyCellDragMaxCols\":5000,\"emptyCellDragMaxRows\":5000,\"draggable\":{\"delayStart\":100,\"enabled\":true,\"ignoreContent\":true,\"dragHandleClass\":\"drag-handler\"},\"resizable\":{\"delayStart\":0,\"enabled\":true},\"swap\":false,\"pushItems\":true,\"disablePushOnDrag\":false,\"disablePushOnResize\":false,\"pushDirections\":{\"north\":true,\"east\":true,\"south\":true,\"west\":true},\"pushResizeItems\":false,\"displayGrid\":\"always\",\"disableWindowResize\":false,\"disableWarnings\":false,\"scrollToNewItems\":true,\"api\":{}}}";

	public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";

	@Override
	public List<Dashboard> findDashboardWithIdentificationAndDescription(String identification, String description,
			String userId) {
		List<Dashboard> dashboards;
		User user = this.userRepository.findByUserId(userId);

		if (user.getRole().getName().equals(DashboardServiceImpl.ADMINISTRATOR)) {
			if (description != null && identification != null) {

				dashboards = this.dashboardRepository
						.findByIdentificationContainingAndDescriptionContaining(identification, description);

			} else if (description == null && identification != null) {

				dashboards = this.dashboardRepository.findByIdentificationContaining(identification);

			} else if (description != null && identification == null) {

				dashboards = this.dashboardRepository.findByDescriptionContaining(description);

			} else {

				dashboards = this.dashboardRepository.findAll();
			}
		} else {
			if (description != null && identification != null) {

				dashboards = this.dashboardRepository.findByUserAndIdentificationContainingAndDescriptionContaining(
						user, identification, description);

			} else if (description == null && identification != null) {

				dashboards = this.dashboardRepository.findByUserAndIdentificationContaining(user, identification);

			} else if (description != null && identification == null) {

				dashboards = this.dashboardRepository.findByUserAndDescriptionContaining(user, description);

			} else {

				dashboards = this.dashboardRepository.findByUser(user);
			}
		}
		return dashboards;
	}

	@Override
	public List<String> getAllIdentifications() {
		List<Dashboard> dashboards = this.dashboardRepository.findAllByOrderByIdentificationAsc();
		List<String> identifications = new ArrayList<String>();
		for (Dashboard dashboard : dashboards) {
			identifications.add(dashboard.getIdentification());

		}
		return identifications;
	}

	@Override
	public List<Dashboard> findAllDashboard() {
		List<Dashboard> dashboard = this.dashboardRepository.findAll();
		return null;
	}

	@Override
	public void deleteDashboard(String dashboardId, String userId) {
		if (hasUserPermission(dashboardId, userId)) {
			Dashboard dashboard = this.dashboardRepository.findById(dashboardId);
			if (dashboard != null) {
				this.dashboardRepository.delete(dashboard);
			} else
				throw new GadgetDatasourceServiceException("Cannot delete dashboard that does not exist");
		}
	}

	@Override
	public boolean hasUserPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if (user.getRole().getName().equals("ROLE_ADMINISTRATOR")) {
			return true;
		} else {
			return dashboardRepository.findById(id).getUser().getUserId().equals(userId);
		}
	}

	@Override
	public void saveDashboard(String id, Dashboard dashboard, String userId) {
		if (hasUserPermission(id, userId)) {
			Dashboard dashboardEnt = this.dashboardRepository.findById(dashboard.getId());
			dashboardEnt.setCustomcss(dashboard.getCustomcss());
			dashboardEnt.setCustomjs(dashboard.getCustomjs());
			dashboardEnt.setDescription(dashboard.getDescription());
			dashboardEnt.setJsoni18n(dashboard.getJsoni18n());
			dashboardEnt.setModel(dashboard.getModel());
			dashboardEnt.setPublic(dashboard.isPublic());
			this.dashboardRepository.save(dashboardEnt);
		} else
			throw new GadgetDatasourceServiceException(
					"Cannot update Dashboard that does not exist or don't have permission");
	}

	@Override
	public void saveDashboardModel(String id, String model, boolean visible, String userId) {
		if (hasUserPermission(id, userId)) {
			Dashboard dashboardEnt = this.dashboardRepository.findById(id);
			dashboardEnt.setModel(model);
			dashboardEnt.setPublic(visible);
			this.dashboardRepository.save(dashboardEnt);
		} else
			throw new GadgetDatasourceServiceException(
					"Cannot update Dashboard that does not exist or don't have permission");
	}

	@Override
	public Dashboard getDashboardById(String id, String userId) {
		// if (hasUserPermission(id, userId)) {
		return dashboardRepository.findById(id);
		// }
		// return null;
	}

	@Override
	public String getCredentialsString(String userId) {
		User user = this.userRepository.findByUserId(userId);
		return userId;
	}

	@Override
	public boolean dashboardExists(String identification) {
		if (this.dashboardRepository.findByIdentification(identification).size() != 0)
			return true;
		else
			return false;
	}

	@Override
	public String createNewDashboard(String identification, String userId) {
		if (!this.dashboardExists(identification)) {
			log.debug("Dashboard no exist, creating...");
			Dashboard d = new Dashboard();
			d.setCustomcss("");
			d.setCustomjs("");
			d.setJsoni18n("");
			d.setDescription("");
			d.setDescription("");
			d.setIdentification(identification);
			d.setPublic(false);
			d.setUser(userRepository.findByUserId(userId));
			d.setModel(initialModel);
			this.dashboardRepository.save(d);
			return d.getId();
		} else
			throw new UserServiceException("Dashboard already exists in Database");
	}
}