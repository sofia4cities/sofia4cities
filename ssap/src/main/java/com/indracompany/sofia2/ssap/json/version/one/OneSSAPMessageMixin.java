package com.indracompany.sofia2.ssap.json.version.one;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;

public abstract class OneSSAPMessageMixin {
	@JsonProperty(required=true, defaultValue="NONE") public abstract SSAPMessageTypes getMessageType();
}
