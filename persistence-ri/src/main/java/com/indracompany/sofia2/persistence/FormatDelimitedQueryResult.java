package com.indracompany.sofia2.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class FormatDelimitedQueryResult {

	@Getter @Setter private String data;
	@Getter @Setter private boolean isDataJson;
	@Getter @Setter private String contentType;
	@Getter @Setter private boolean ok;
}
