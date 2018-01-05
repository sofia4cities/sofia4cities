package com.indracompany.sofia2.ssap.json.version.one;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.indracompany.sofia2.ssap.body.SSAPBodyEmptyMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyJoinMessage;
import com.indracompany.sofia2.ssap.body.SSAPBodyLeaveMessage;

@JsonTypeInfo(use=Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(value=SSAPBodyJoinMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyEmptyMessage.class),
	@JsonSubTypes.Type(value=SSAPBodyLeaveMessage.class),
})
public abstract class OneSSAPBodyMessageMixin {
	@JsonIgnore public abstract boolean isThinKpMandatory();
	@JsonIgnore public abstract boolean isSessionKeyMandatory();	
	@JsonIgnore public abstract boolean isAutorizationMandatory();
}
