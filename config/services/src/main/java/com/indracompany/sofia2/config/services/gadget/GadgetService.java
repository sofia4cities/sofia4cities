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
package com.indracompany.sofia2.config.services.gadget;

import java.util.List;

import com.indracompany.sofia2.config.model.Gadget;
import com.indracompany.sofia2.config.model.GadgetMeasure;

public interface GadgetService {

	public List<Gadget> findAllGadgets();

	public List<Gadget> findGadgetWithIdentificationAndDescription(String identification, String description,
			String user);

	public List<String> getAllIdentifications();

	public Gadget getGadgetById(String userID, String gadgetId);

	public void createGadget(Gadget gadget);

	public List<Gadget> getUserGadgetsByType(String userID, String type);

	public List<GadgetMeasure> getGadgetMeasuresByGadgetId(String userID, String gadgetId);

	public boolean hasUserPermission(String id, String userId);

	public void updateGadget(Gadget gadget, String gadgetDatasourceIds, String jsonMeasures);

	public void createGadget(Gadget gadget, String gadgetDatasourceIds, String jsonMeasures);

	public void deleteGadget(String gadgetId, String userId);
}
