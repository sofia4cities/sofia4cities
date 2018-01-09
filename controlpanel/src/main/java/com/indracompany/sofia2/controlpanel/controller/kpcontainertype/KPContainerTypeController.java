package com.indracompany.sofia2.controlpanel.controller.kpcontainertype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.indracompany.sofia2.config.repository.KPContainerTypeRepository;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class KPContainerTypeController {

    private static final String LIST = "kpcontainertype/list";
    private static final String MODEL_ITEM_LIST = "itemList";

    @Autowired
    private KPContainerTypeRepository repository;

    @RequestMapping(value = {"/kpcontainertype/list"}, method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute(MODEL_ITEM_LIST, repository.findAll());
        return LIST;
    }
}
