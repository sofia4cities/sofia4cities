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
package com.indracompany.sofia2.config.services.gadgettemplate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.GadgetTemplate;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.GadgetTemplateRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.services.exceptions.GadgetTemplateServiceException;
import com.indracompany.sofia2.config.services.gadget.GadgetServiceImpl;

@Service
public class GadgetTemplateServiceImpl implements GadgetTemplateService {

	@Autowired
	private GadgetTemplateRepository gadgetTemplateRepository;

	@Autowired
	private UserRepository userRepository;

	public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";

	@Override
	public List<GadgetTemplate> findAllGadgetTemplates() {
		List<GadgetTemplate> gadgetTemplates = this.gadgetTemplateRepository.findAll();
		return gadgetTemplates;
	}

	@Override
	public List<GadgetTemplate> findGadgetTemplateWithIdentificationAndDescription(String identification,
			String description, String userId) {
		List<GadgetTemplate> gadgetTemplates;
		User user = this.userRepository.findByUserId(userId);

		if (user.getRole().getName().equals(GadgetServiceImpl.ADMINISTRATOR)) {
			if (description != null && identification != null) {

				gadgetTemplates = this.gadgetTemplateRepository
						.findByIdentificationContainingAndDescriptionContaining(identification, description);

			} else if (description == null && identification != null) {

				gadgetTemplates = this.gadgetTemplateRepository.findByIdentificationContaining(identification);

			} else if (description != null && identification == null) {

				gadgetTemplates = this.gadgetTemplateRepository.findByDescriptionContaining(description);

			} else {

				gadgetTemplates = this.gadgetTemplateRepository.findAll();
			}
		} else {
			if (description != null && identification != null) {

				gadgetTemplates = this.gadgetTemplateRepository
						.findByUserAndIdentificationContainingAndDescriptionContaining(user, identification,
								description);

			} else if (description == null && identification != null) {

				gadgetTemplates = this.gadgetTemplateRepository.findByUserAndIdentificationContaining(user,
						identification);

			} else if (description != null && identification == null) {

				gadgetTemplates = this.gadgetTemplateRepository.findByUserAndDescriptionContaining(user, description);

			} else {

				gadgetTemplates = this.gadgetTemplateRepository.findByUser(user);
			}
		}
		return gadgetTemplates;
	}

	@Override
	public List<String> getAllIdentifications() {
		List<GadgetTemplate> gadgetTemplates = this.gadgetTemplateRepository.findAllByOrderByIdentificationAsc();
		List<String> names = new ArrayList<String>();
		for (GadgetTemplate gadgetTemplate : gadgetTemplates) {
			names.add(gadgetTemplate.getIdentification());
		}
		return names;
	}

	@Override
	public GadgetTemplate getGadgetTemplateById(String id) {
		GadgetTemplate gadgetTemplate = this.gadgetTemplateRepository.findById(id);
		return gadgetTemplate;
	}

	@Override
	public boolean hasUserPermission(String id, String userId) {
		User user = userRepository.findByUserId(userId);
		if (user.getRole().getName().equals("ROLE_ADMINISTRATOR")) {
			return true;
		} else {
			return gadgetTemplateRepository.findById(id).getUser().getUserId().equals(userId);
		}
	}

	@Override
	public void updateGadgetTemplate(GadgetTemplate gadgettemplate) {
		gadgetTemplateRepository.save(gadgettemplate);
	}

	@Override
	public void createGadgetTemplate(GadgetTemplate gadgettemplate) {
		gadgetTemplateRepository.save(gadgettemplate);

	}

	@Override
	public void deleteGadgetTemplate(String id, String userId) {
		if (hasUserPermission(id, userId)) {
			GadgetTemplate gadgetTemplate = this.gadgetTemplateRepository.findById(id);
			if (gadgetTemplate != null) {
				this.gadgetTemplateRepository.delete(gadgetTemplate);
			} else
				throw new GadgetTemplateServiceException("Cannot delete gadgetTemplate that does not exist");
		}

	}

	@Override
	public List<GadgetTemplate> getUserGadgetTemplate(String userId) {
		List<GadgetTemplate> gadgetTemplates = this.gadgetTemplateRepository
				.findGadgetTemplateByUserAndIsPublicTrue(userId);
		return gadgetTemplates;
	}

}
