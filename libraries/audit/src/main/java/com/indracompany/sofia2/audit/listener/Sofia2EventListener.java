package com.indracompany.sofia2.audit.listener;

import org.springframework.beans.factory.annotation.Autowired;

import com.indracompany.sofia2.audit.notify.EventRouter;

public class Sofia2EventListener {
	
	@Autowired
	protected EventRouter eventRouter;

}
