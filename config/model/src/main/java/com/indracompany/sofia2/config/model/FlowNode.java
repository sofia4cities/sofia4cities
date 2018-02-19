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
package com.indracompany.sofia2.config.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Configurable
@Entity
@Table(name = "FLOW_NODE")
public class FlowNode extends AuditableEntityWithUUID {

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "FLOW_ID", referencedColumnName = "ID", nullable = false)
	@Getter
	@Setter
	private Flow flow;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JoinColumn(name = "FLOW_NODE_TYPE", referencedColumnName = "ID", nullable = false)
	@Getter
	@Setter
	private FlowNodeType flowNodeType;

	@NotNull
	@Getter
	@Setter
	@Column(name = "NODE_RED_NODE_ID", length = 50, unique = true, nullable = false)
	private String nodeRedNodeId;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "flowNode", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@MapKey(name = "name")
	private Map<String, FlowNodeProperties> flowNodeProperties;

}
