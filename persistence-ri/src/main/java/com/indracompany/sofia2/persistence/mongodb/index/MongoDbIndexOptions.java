/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.index;

import java.util.concurrent.TimeUnit;

import com.mongodb.client.model.IndexOptions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class MongoDbIndexOptions {

	@Getter @Setter private Boolean unique;
	@Getter @Setter private Boolean sparse;
	@Getter @Setter private Boolean background;
	@Getter @Setter private Long expireAfterSeconds;


	public IndexOptions toNativeIndexOptions() {
		IndexOptions nativeIndexOptions = new IndexOptions();
		if (getSparse() != null)
			nativeIndexOptions.sparse(getSparse());
		if (getUnique() != null)
			nativeIndexOptions.unique(getUnique());
		if (getBackground() != null)
			nativeIndexOptions.background(getBackground());
		if (getExpireAfterSeconds() != null)
			nativeIndexOptions.expireAfter(getExpireAfterSeconds(), TimeUnit.SECONDS);
		return nativeIndexOptions;
	}
	

	public boolean isDefaultConfiguration() {
		return this.background == null && this.sparse == null && this.unique == null && this.expireAfterSeconds == null;
	}
	
}
