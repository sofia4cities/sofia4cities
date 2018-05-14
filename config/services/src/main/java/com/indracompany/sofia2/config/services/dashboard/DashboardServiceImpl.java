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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.Dashboard;
import com.indracompany.sofia2.config.model.DashboardUserAccess;
import com.indracompany.sofia2.config.model.DashboardUserAccessType;
import com.indracompany.sofia2.config.model.Role;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.DashboardRepository;
import com.indracompany.sofia2.config.repository.DashboardUserAccessRepository;
import com.indracompany.sofia2.config.repository.DashboardUserAccessTypeRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardAccessDTO;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardCreateDTO;
import com.indracompany.sofia2.config.services.dashboard.dto.DashboardDTO;
import com.indracompany.sofia2.config.services.exceptions.DashboardServiceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private DashboardRepository dashboardRepository;
	@Autowired
	private DashboardUserAccessRepository dashboardUserAccessRepository;
	@Autowired
	private DashboardUserAccessTypeRepository dashboardUserAccessTypeRepository;
	@Autowired
	private UserRepository userRepository;

	private static final String initialModel = "{\"header\":{\"title\":\"My new s4c Dashboard\",\"enable\":true,\"height\":56,\"logo\":{\"height\":48},\"backgroundColor\":\"hsl(220, 23%, 20%)\",\"textColor\":\"hsl(0, 0%, 100%)\",\"iconColor\":\"hsl(0, 0%, 100%)\",\"pageColor\":\"hsl(0, 0%, 100%)\"},\"navigation\":{\"showBreadcrumbIcon\":true,\"showBreadcrumb\":true},\"pages\":[{\"title\":\"New Page\",\"icon\":\"apps\",\"background\":{\"file\":[]},\"layers\":[{\"gridboard\":[{}],\"title\":\"baseLayer\",\"$$hashKey\":\"object:23\"}],\"selectedlayer\":0,\"combinelayers\":false,\"$$hashKey\":\"object:4\"}],\"gridOptions\":{\"gridType\":\"fit\",\"compactType\":\"none\",\"margin\":3,\"outerMargin\":true,\"mobileBreakpoint\":640,\"minCols\":20,\"maxCols\":100,\"minRows\":20,\"maxRows\":100,\"maxItemCols\":5000,\"minItemCols\":1,\"maxItemRows\":5000,\"minItemRows\":1,\"maxItemArea\":25000,\"minItemArea\":1,\"defaultItemCols\":4,\"defaultItemRows\":4,\"fixedColWidth\":250,\"fixedRowHeight\":250,\"enableEmptyCellClick\":false,\"enableEmptyCellContextMenu\":false,\"enableEmptyCellDrop\":true,\"enableEmptyCellDrag\":false,\"emptyCellDragMaxCols\":5000,\"emptyCellDragMaxRows\":5000,\"draggable\":{\"delayStart\":100,\"enabled\":true,\"ignoreContent\":true,\"dragHandleClass\":\"drag-handler\"},\"resizable\":{\"delayStart\":0,\"enabled\":true},\"swap\":false,\"pushItems\":true,\"disablePushOnDrag\":false,\"disablePushOnResize\":false,\"pushDirections\":{\"north\":true,\"east\":true,\"south\":true,\"west\":true},\"pushResizeItems\":false,\"displayGrid\":\"none\",\"disableWindowResize\":false,\"disableWarnings\":false,\"scrollToNewItems\":true,\"api\":{}},\"interactionHash\":{\"1\":[]}}";
	private static final String ANONYMOUSUSER = "anonymousUser";

	@Override
	public List<DashboardDTO> findDashboardWithIdentificationAndDescription(String identification, String description,
			String userId) {
		List<Dashboard> dashboards;
		User sessionUser = this.userRepository.findByUserId(userId);

		description = description == null ? "" : description;
		identification = identification == null ? "" : identification;

		if (sessionUser.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			dashboards = this.dashboardRepository.findByIdentificationContainingAndDescriptionContaining(identification,
					description);
		} else {
			dashboards = dashboardRepository
					.findByUserAndPermissionsANDIdentificationContainingAndDescriptionContaining(sessionUser,
							identification, description);
		}

		List<DashboardDTO> dashboardsDTO = dashboards.stream().map(temp -> {
			DashboardDTO obj = new DashboardDTO();
			obj.setCreatedAt(temp.getCreatedAt());
			// obj.setCustomcss(temp.getCustomcss());
			// obj.setCustomjs(customjs);
			obj.setDescription(temp.getDescription());
			obj.setId(temp.getId());
			obj.setIdentification(temp.getIdentification());
			// obj.setJsoni18n(jsoni18n);
			// obj.setModel(model);
			obj.setPublic(temp.isPublic());
			obj.setUpdatedAt(temp.getUpdatedAt());
			obj.setUser(temp.getUser());
			obj.setUserAccessType(getUserTypePermissionForDashboard(temp, sessionUser));
			return obj;
		}).collect(Collectors.toList());

		return dashboardsDTO;
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
	public void deleteDashboard(String dashboardId, String userId) {
		if (hasUserEditPermission(dashboardId, userId)) {
			Dashboard dashboard = this.dashboardRepository.findById(dashboardId);
			if (dashboard != null) {
				this.dashboardRepository.delete(dashboard);
			} else
				throw new DashboardServiceException("Cannot delete dashboard that does not exist");
		}
	}

	@Override
	public boolean hasUserPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else {
			return dashboardRepository.findById(id).getUser().getUserId().equals(userId);
		}
	}

	@Override
	public boolean hasUserEditPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else {
			boolean propietary = dashboardRepository.findById(id).getUser().getUserId().equals(userId);
			if (propietary) {
				return true;
			}
			DashboardUserAccess userAuthorization = dashboardUserAccessRepository
					.findByDashboardAndUser(dashboardRepository.findById(id), user);

			if (userAuthorization != null) {
				switch (DashboardUserAccessType.Type
						.valueOf(userAuthorization.getDashboardUserAccessType().getName())) {
				case EDIT:
					return true;
				case VIEW:
				default:
					return false;
				}
			} else {
				return false;
			}

		}
	}

	@Override
	public boolean hasUserViewPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);

		if (userId.equals(ANONYMOUSUSER) || user == null) {
			return dashboardRepository.findById(id).isPublic();
		}

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return true;
		} else {
			boolean propietary = dashboardRepository.findById(id).getUser().getUserId().equals(userId);
			if (propietary) {
				return true;
			}
			DashboardUserAccess userAuthorization = dashboardUserAccessRepository
					.findByDashboardAndUser(dashboardRepository.findById(id), user);

			if (userAuthorization != null) {
				switch (DashboardUserAccessType.Type
						.valueOf(userAuthorization.getDashboardUserAccessType().getName())) {
				case EDIT:
					return true;
				case VIEW:
					return true;
				default:
					return false;
				}
			} else {
				return false;
			}

		}
	}

	public String getUserTypePermissionForDashboard(Dashboard dashboard, User user) {

		if (user.getRole().getId().equals(Role.Type.ROLE_ADMINISTRATOR.toString())) {
			return DashboardUserAccessType.Type.EDIT.toString();
		} else {

			if (dashboard.getUser().getUserId().equals(user.getUserId())) {
				return DashboardUserAccessType.Type.EDIT.toString();
			}
			DashboardUserAccess userAuthorization = dashboardUserAccessRepository.findByDashboardAndUser(dashboard,
					user);

			if (userAuthorization != null) {
				switch (DashboardUserAccessType.Type
						.valueOf(userAuthorization.getDashboardUserAccessType().getName())) {
				case EDIT:
					return DashboardUserAccessType.Type.EDIT.toString();
				case VIEW:
					return DashboardUserAccessType.Type.VIEW.toString();
				default:
					return DashboardUserAccessType.Type.VIEW.toString();
				}
			} else {
				return DashboardUserAccessType.Type.VIEW.toString();
			}

		}
	}

	@Override
	public void saveDashboard(String id, Dashboard dashboard, String userId) {
		if (hasUserEditPermission(id, userId)) {
			Dashboard dashboardEnt = this.dashboardRepository.findById(dashboard.getId());
			dashboardEnt.setCustomcss(dashboard.getCustomcss());
			dashboardEnt.setCustomjs(dashboard.getCustomjs());
			dashboardEnt.setDescription(dashboard.getDescription());
			dashboardEnt.setJsoni18n(dashboard.getJsoni18n());
			dashboardEnt.setModel(dashboard.getModel());
			dashboardEnt.setPublic(dashboard.isPublic());
			this.dashboardRepository.save(dashboardEnt);
		} else
			throw new DashboardServiceException("Cannot update Dashboard that does not exist or don't have permission");
	}

	@Override
	public void saveDashboardModel(String id, String model, boolean visible, String userId) {
		if (hasUserEditPermission(id, userId)) {
			Dashboard dashboardEnt = this.dashboardRepository.findById(id);
			dashboardEnt.setModel(model);
			dashboardEnt.setPublic(visible);
			this.dashboardRepository.save(dashboardEnt);
		} else
			throw new DashboardServiceException("Cannot update Dashboard that does not exist or don't have permission");
	}

	@Override
	public Dashboard getDashboardById(String id, String userId) {
		return dashboardRepository.findById(id);
	}

	@Override
	public Dashboard getDashboardEditById(String id, String userId) {
		if (hasUserEditPermission(id, userId)) {
			return dashboardRepository.findById(id);
		}
		throw new DashboardServiceException("Cannot view Dashboard that does not exist or don't have permission");
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
	public String createNewDashboard(DashboardCreateDTO dashboard, String userId) {
		if (!this.dashboardExists(dashboard.getIdentification())) {

			log.debug("Dashboard no exist, creating...");
			Dashboard d = new Dashboard();
			d.setCustomcss("");
			d.setCustomjs("");
			d.setJsoni18n("");
			d.setDescription(dashboard.getDescription());
			d.setIdentification(dashboard.getIdentification());
			d.setPublic(dashboard.getPublicAccess());
			d.setUser(userRepository.findByUserId(userId));
			d.setModel(initialModel);
			this.dashboardRepository.save(d);

			ObjectMapper objectMapper = new ObjectMapper();

			try {
				if (dashboard.getAuthorizations() != null) {
					List<DashboardAccessDTO> access = objectMapper.readValue(dashboard.getAuthorizations(), objectMapper
							.getTypeFactory().constructCollectionType(List.class, DashboardAccessDTO.class));
					for (Iterator iterator = access.iterator(); iterator.hasNext();) {
						DashboardAccessDTO dashboardAccessDTO = (DashboardAccessDTO) iterator.next();
						DashboardUserAccess dua = new DashboardUserAccess();
						dua.setDashboard(d);
						List<DashboardUserAccessType> managedTypes = dashboardUserAccessTypeRepository
								.findByName(dashboardAccessDTO.getAccesstypes());
						DashboardUserAccessType managedType = managedTypes != null && managedTypes.size() > 0
								? managedTypes.get(0)
								: null;
						dua.setDashboardUserAccessType(managedType);
						dua.setUser(userRepository.findByUserId(dashboardAccessDTO.getUsers()));
						this.dashboardUserAccessRepository.save(dua);
					}
				}

			} catch (JsonParseException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			} catch (JsonMappingException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			} catch (IOException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			}

			return d.getId();
		} else
			throw new DashboardServiceException("Dashboard already exists in Database");
	}

	@Override
	public List<DashboardUserAccess> getDashboardUserAccesses(String dashboardId) {
		Dashboard dashboard = dashboardRepository.findById(dashboardId);
		List<DashboardUserAccess> authorizations = dashboardUserAccessRepository.findByDashboard(dashboard);
		return authorizations;
	}

	@Transactional
	@Override
	public String cleanDashboardAccess(DashboardCreateDTO dashboard, String userId) {
		if (!this.dashboardExists(dashboard.getIdentification())) {
			throw new DashboardServiceException("Dashboard does not exist in the database");
		} else {

			Dashboard d = dashboardRepository.findById(dashboard.getId());
			this.dashboardUserAccessRepository.deleteByDashboard(d);
			return d.getId();

		}
	}

	@Transactional
	@Override
	public String saveUpdateAccess(DashboardCreateDTO dashboard, String userId) {
		if (!this.dashboardExists(dashboard.getIdentification())) {
			throw new DashboardServiceException("Dashboard does not exist in the database");
		} else {

			Dashboard d = dashboardRepository.findById(dashboard.getId());
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				if (dashboard.getAuthorizations() != null) {
					List<DashboardAccessDTO> access = objectMapper.readValue(dashboard.getAuthorizations(), objectMapper
							.getTypeFactory().constructCollectionType(List.class, DashboardAccessDTO.class));
					for (Iterator iterator = access.iterator(); iterator.hasNext();) {
						DashboardAccessDTO dashboardAccessDTO = (DashboardAccessDTO) iterator.next();
						DashboardUserAccess dua = new DashboardUserAccess();
						dua.setDashboard(dashboardRepository.findById(dashboard.getId()));
						List<DashboardUserAccessType> managedTypes = dashboardUserAccessTypeRepository
								.findByName(dashboardAccessDTO.getAccesstypes());
						DashboardUserAccessType managedType = managedTypes != null && managedTypes.size() > 0
								? managedTypes.get(0)
								: null;
						dua.setDashboardUserAccessType(managedType);
						dua.setUser(userRepository.findByUserId(dashboardAccessDTO.getUsers()));
						this.dashboardUserAccessRepository.save(dua);
					}
				}
				return d.getId();

			} catch (JsonParseException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			} catch (JsonMappingException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			} catch (IOException e) {
				throw new DashboardServiceException("Authorizations parse Exception");
			}

		}
	}

	@Transactional
	@Override
	public String updatePublicDashboard(DashboardCreateDTO dashboard, String userId) {
		if (!this.dashboardExists(dashboard.getIdentification())) {
			throw new DashboardServiceException("Dashboard does not exist in the database");
		} else {
			Dashboard d = dashboardRepository.findById(dashboard.getId());
			d.setPublic(dashboard.getPublicAccess());
			d.setDescription(dashboard.getDescription());
			this.dashboardRepository.save(d);
			return d.getId();
		}
	}

}