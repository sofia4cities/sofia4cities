package com.indracompany.sofia2.controlpanel.controller.RTDBController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/databases")
public class RTDBConsoleController {
	
	@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
	public String show(Model model)
	{
		return "/databases/show";
		
	}

}
