package com.indracompany.sofia2.digitaltwin.event.manager;

import java.util.Map;

public interface EventManager {
	
	public void updateShadow(Map<String, Object> status);
	public void log(String trace);

}
