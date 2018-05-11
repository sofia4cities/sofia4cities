/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notebook")
public class Notebook extends AuditableEntityWithUUID  {

	private static final long serialVersionUID = -424404777731521676L;

    @Column(name = "IDENTIFICATION", length = 100,nullable = false)
    @Getter @Setter private String identification;
	
    @ManyToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable=false)
	@Getter @Setter private User user;
	
	@Column(name = "IDZEP", length = 100,nullable = false)
	@Getter	@Setter private String idzep;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (identification.hashCode());
		result = prime * result + (user.hashCode());
		result = prime * result + (idzep.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notebook other = (Notebook) obj;
		if (identification == null) {
			if (other.identification != null)
				return false;
		} else if (!identification.equals(other.identification))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (idzep == null) {
			if (other.idzep != null)
				return false;
		} else if (!idzep.equals(other.idzep))
			return false;
		return true;
	}

   
}
