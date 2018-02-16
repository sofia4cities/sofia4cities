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
