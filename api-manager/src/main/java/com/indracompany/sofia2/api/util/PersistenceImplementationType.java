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
package com.indracompany.sofia2.api.util;
public enum PersistenceImplementationType {
    MONGODB,
    ORACLE,
    POSTGRESQL,
    KUDU;
	public String getConsoleString() {
		switch (this) {
		case KUDU:
			return "relationalKudu";
		case ORACLE:
			return "multibdtr";
		case POSTGRESQL:
			return "multibdtr";
		default:
			return "documentalMongo";
		}
	}
	public String toString() {
		switch(this) {
		case KUDU:
			return "Kudu";
		case ORACLE:
			return "Oracle";
		case POSTGRESQL:
			return "Postgresql";
		default:
			return "MongoDB";
		}
	}
}