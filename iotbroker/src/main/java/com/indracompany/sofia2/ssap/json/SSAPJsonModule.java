package com.indracompany.sofia2.ssap.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.indracompany.sofia2.ssap.body.parent.SSAPBodyMessage;
import com.indracompany.sofia2.ssap.json.version.one.OneSSAPBodyMessageMixin;

public class SSAPJsonModule extends Module{
	private static final String NAME = "JACKSON_MODULE";
	private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
	
	public SSAPJsonModule() {
		
	}
	
	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION_UTIL.version();
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(SSAPBodyMessage.class, OneSSAPBodyMessageMixin.class);
	}

}
