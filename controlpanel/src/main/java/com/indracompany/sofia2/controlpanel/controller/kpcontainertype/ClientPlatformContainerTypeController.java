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
package com.indracompany.sofia2.controlpanel.controller.kpcontainertype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.indracompany.sofia2.config.repository.ClientPlatformContainerTypeRepository;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class ClientPlatformContainerTypeController {

    private static final String LIST = "kpcontainertype/list";
    private static final String MODEL_ITEM_LIST = "itemList";

    @Autowired
    private ClientPlatformContainerTypeRepository repository;

    @RequestMapping(value = {"/kpcontainertype/list"}, method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute(MODEL_ITEM_LIST, repository.findAll());
        return LIST;
    }
}
