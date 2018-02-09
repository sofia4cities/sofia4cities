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
package com.indracompany.sofia2.flowengine.api.rest.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.Flow;
import com.indracompany.sofia2.config.model.FlowDomain;
import com.indracompany.sofia2.config.model.FlowNode;
import com.indracompany.sofia2.config.model.FlowNodeProperties;
import com.indracompany.sofia2.config.model.FlowNodeType;
import com.indracompany.sofia2.config.model.Ontology;
import com.indracompany.sofia2.config.model.OntologyUserAccess;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.FlowDomainRepository;
import com.indracompany.sofia2.config.repository.FlowNodeRepository;
import com.indracompany.sofia2.config.repository.FlowNodeTypeRepository;
import com.indracompany.sofia2.config.repository.FlowRepository;
import com.indracompany.sofia2.config.repository.OntologyRepository;
import com.indracompany.sofia2.config.repository.OntologyUserAccessRepository;
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.config.security.UserRole;
import com.indracompany.sofia2.flowengine.api.rest.pojo.DeployRequestRecord;
import com.indracompany.sofia2.flowengine.api.rest.pojo.UserDomainValidationRequest;
import com.indracompany.sofia2.flowengine.api.rest.service.FlowEngineNodeService;
import com.indracompany.sofia2.flowengine.exception.NotAllowedException;
import com.indracompany.sofia2.flowengine.exception.NotAuthorizedException;
import com.indracompany.sofia2.flowengine.exception.ResourceNotFoundException;
import com.indracompany.sofia2.persistence.exceptions.DBPersistenceException;
import com.indracompany.sofia2.persistence.interfaces.BasicOpsDBRepository;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Service
@Slf4j
public class FlowEngineNodeServiceImpl implements FlowEngineNodeService {

	@Autowired
	@Qualifier("MongoBasicOpsDBRepository")
	private BasicOpsDBRepository basicRDBRepository;

	@Autowired
	private FlowDomainRepository domainRepository;

	@Autowired
	private FlowRepository flowRepository;

	@Autowired
	private FlowNodeRepository nodeRepository;

	@Autowired
	private FlowNodeTypeRepository nodeTypeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OntologyRepository ontologyRepository;

	@Autowired
	private ClientPlatformRepository clientPlatformRepository;

	@Autowired
	private OntologyUserAccessRepository userAccessRepository;

	@Override
	public String deploymentNotification(String json) {
		// TODO Change Return type
		ObjectMapper mapper = new ObjectMapper();
		FlowDomain domain = null;
		List<DeployRequestRecord> deployRecords = new ArrayList<>();

		List<FlowNodeType> sofia2NodeTypes = nodeTypeRepository.findAll();

		try {
			deployRecords = mapper.readValue(json, new TypeReference<List<DeployRequestRecord>>() {
			});
			for (DeployRequestRecord record : deployRecords) {
				if (record != null) {
					if (record.getDomain() != null) {
						log.info("Deployment info from domain = {}", record.getDomain());
						domain = domainRepository.findByIdentification(record.getDomain());
						// Delete all data from this Domain,
						// including flows, nodes and properties
						List<Flow> flows = flowRepository.findByFlowDomain_Identification(domain.getIdentification());
						for (Flow flow : flows) {
							List<FlowNode> nodes = nodeRepository.findByFlow_NodeRedFlowId(flow.getNodeRedFlowId());
							for (FlowNode node : nodes) {
								nodeRepository.delete(node);
							}
							flowRepository.delete(flow);
						}
					} else {
						log.debug("Deployment record = {}", record.toString());
						if (record.getType() != null) {
							if (record.getType().equals("tab")) {
								// its a FLOW
								Flow newFlow = new Flow();
								newFlow.setIdentification(record.getLabel());
								newFlow.setNodeRedFlowId(record.getId());
								newFlow.setActive(true);
								newFlow.setFlowDomain(domain);
								flowRepository.save(newFlow);
							} else {
								// its a regular node.
								for (FlowNodeType type : sofia2NodeTypes) {
									// Only Sofia2 Nodes will be persisted
									if (type.getIdentification().equals(record.getType())) {
										// Create new node and persist
										FlowNode node = new FlowNode();
										FlowNodeProperties nodeProperty;
										Map<String, FlowNodeProperties> flowNodeProperties = new HashMap<>();

										Flow flow = flowRepository.findByNodeRedFlowId(record.getZ()).get(0);
										node.setNodeRedNodeId(record.getId());
										node.setFlowNodeType(nodeTypeRepository.findByIdentification(record.getType()));
										node.setFlow(flow);

										if (record.getType().equals("ssap-process-request")) {
											nodeProperty = FlowNodeProperties.builder().flowNode(node).name("direction")
													.value(record.getDirection()).build();
											flowNodeProperties.put("direction", nodeProperty);
											nodeProperty = FlowNodeProperties.builder().flowNode(node)
													.name("messageType").value(record.getMeassageType()).build();
											flowNodeProperties.put("messageType", nodeProperty);
											nodeProperty = FlowNodeProperties.builder().flowNode(node).name("ontology")
													.value(record.getOntology()).build();
											flowNodeProperties.put("ontology", nodeProperty);
											nodeProperty = FlowNodeProperties.builder().flowNode(node).name("thinKp")
													.value(record.getThinKp()).build();
											flowNodeProperties.put("thinKp", nodeProperty);
											nodeProperty = FlowNodeProperties.builder().flowNode(node)
													.name("kpInstance").value(record.getKpInstance()).build();
											flowNodeProperties.put("kpInstance", nodeProperty);
										} else if (record.getType().equals("script-topic")) {
											nodeProperty = FlowNodeProperties.builder().flowNode(node).name("topic")
													.value(record.getTopic()).build();
											flowNodeProperties.put("topic", nodeProperty);
										}

										node.setFlowNodeProperties(flowNodeProperties);
										nodeRepository.save(node);
									}
								}
							}
						} else {
							log.warn("Undefined type for NodeRed element. Record will be skipped : {}",
									record.toString());
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Change return statement
			log.error("Unable to save deployment info from NodeRed into CDB. Cause = {}, message = {}", e.getCause(),
					e.getMessage());
			return "NOTOK";
		}
		return "ok";
	}

	@Override
	public List<String> getOntologyByUser(String userId, String password)
			throws ResourceNotFoundException, NotAuthorizedException {

		List<String> response = new ArrayList<>();

		User sofia2User = validateUserCredentials(userId, password);

		UserRole.valueOf(sofia2User.getRole().getName());
		List<Ontology> ontologies = null;
		switch (UserRole.valueOf(sofia2User.getRole().getName())) {
		case ROLE_ADMINISTRATOR:
			ontologies = ontologyRepository.findByActiveTrue();
			break;
		default:
			// TODO check default criteria
			ontologies = ontologyRepository.findByUserAndOntologyUserAccessAndAllPermissions(sofia2User);
			break;
		}
		for (Ontology ontology : ontologies) {
			response.add(ontology.getIdentification());
		}
		Collections.sort(response);
		return response;
	}

	@Override
	public List<String> getClientPlatformByUser(String userId, String password)
			throws ResourceNotFoundException, NotAuthorizedException {

		List<String> response = new ArrayList<>();

		User sofia2User = validateUserCredentials(userId, password);

		UserRole.valueOf(sofia2User.getRole().getName());
		List<ClientPlatform> clientPlatforms = null;
		switch (UserRole.valueOf(sofia2User.getRole().getName())) {
		case ROLE_ADMINISTRATOR:
			clientPlatforms = clientPlatformRepository.findAll();
			break;
		default:
			// TODO check default criteria
			clientPlatforms = clientPlatformRepository.findByUser(sofia2User);
			break;
		}
		for (ClientPlatform clientPlatform : clientPlatforms) {
			response.add(clientPlatform.getIdentification());
		}
		Collections.sort(response);
		return response;
	}

	@Override
	public String validateUserDomain(UserDomainValidationRequest request)
			throws ResourceNotFoundException, NotAuthorizedException, NotAllowedException {

		String response = null;
		User sofia2User = validateUserCredentials(request.getUserId(), request.getPassword());
		if (request.getDomainId() == null) {
			throw new IllegalArgumentException("DomainId must be specified.");
		}

		FlowDomain domain = domainRepository.findByIdentification(request.getDomainId());

		if (domain == null) {
			throw new ResourceNotFoundException(
					"Domain with identification " + request.getDomainId() + " could not be found.");
		}

		switch (UserRole.valueOf(sofia2User.getRole().getName())) {
		case ROLE_ADMINISTRATOR:
			response = "OK"; // Has permission over all domains
			break;
		default:
			if (!domain.getUser().getUserId().equals(sofia2User.getUserId())) {
				throw new NotAllowedException("User " + request.getUserId()
						+ " has no permissions over specified domain " + request.getDomainId());
			}
			response = "OK";
			break;
		}
		return response;
	}

	@Override
	public String submitQuery(String ontologyIdentificator, String queryType, String query, String user,
			String password) throws ResourceNotFoundException, NotAuthorizedException, NotFoundException,
			JsonProcessingException, DBPersistenceException {
		// TODO Auto-generated method stub
		ObjectMapper mapper = new ObjectMapper();
		User sofia2User = validateUserCredentials(user, password);
		String privilleges = validateOntologyUser(sofia2User, ontologyIdentificator);
		// TODO Change criteria. There should be no conditions harcoded refered
		// to a certain query language
		if ("SQL".equals(queryType)) {
			if (query.toLowerCase().startsWith("select")) {
				if (!privilleges.equals("INSERT")) {
					return basicRDBRepository.querySQLAsJson(ontologyIdentificator, query);

				} else {
					log.error("User {} has no QUERY/ALL access over {} ontology.", user, ontologyIdentificator);
				}
			} else if (query.toLowerCase().startsWith("update") || query.toLowerCase().startsWith("insert")) {
				if (!privilleges.equals("QUERY")) {
					return basicRDBRepository.querySQLAsJson(ontologyIdentificator, query);
				} else {
					log.error("User {} has no INSERT/ALL access over {} ontology.", user, ontologyIdentificator);
				}
			}
		} else if ("native".equals(queryType)) {
			if (query.toLowerCase().contains(".find")) {
				return mapper.writeValueAsString(basicRDBRepository.queryNative(ontologyIdentificator, query));
			} else if (query.toLowerCase().contains(".update")) {
				// TODO: Not implemented in RTDB yet
				throw new NotImplementedException();
			} else if (query.toLowerCase().contains(".remove")) {
				// TODO: Not implemented in RTDB yet
				throw new NotImplementedException();
			} else if (query.toLowerCase().contains(".insert")) {
				// TODO: Not implemented in RTDB yet
				throw new NotImplementedException();
			}
		} else {
			log.error("Invalid value {} for queryType. Possible values are: SQL, NATIVE.", queryType);
			throw new IllegalArgumentException(
					"Invalid value " + queryType + " for queryType. Possible values are: SQL, NATIVE.");
		}
		log.error("Query could not be parsed. Query = {}", query);
		throw new IllegalArgumentException("Unrecognized query format.");
	}

	private User validateUserCredentials(String userId, String password)
			throws ResourceNotFoundException, NotAuthorizedException {
		if (userId == null || password == null || userId.isEmpty() || password.isEmpty()) {
			log.error("User or password cannot be empty.");
			throw new IllegalArgumentException("User or password cannot be empty.");
		}

		User sofia2User = userRepository.findByUserId(userId);
		if (sofia2User == null) {
			log.error("Requested user does not exist");
			throw new ResourceNotFoundException("Requested user does not exist");
		}
		if (!sofia2User.getPassword().equals(password)) {
			log.error("Password for user " + userId + " does not match.");
			throw new NotAuthorizedException("Password for user " + userId + " does not match.");
		}
		return sofia2User;
	}

	private String validateOntologyUser(User user, String ontologyIdentification)
			throws ResourceNotFoundException, NotAuthorizedException, NotFoundException {
		Ontology ontology = ontologyRepository.findByIdentification(ontologyIdentification);

		if (ontology == null) {
			log.error("Ontology {} not found.", ontologyIdentification);
			throw new ResourceNotFoundException("Ontology " + ontologyIdentification + " not found.");
		}

		if (ontology.getUser().equals(user)
				|| user.getRole().getName().equals(UserRole.ROLE_ADMINISTRATOR.toString())) {
			return "ALL";
		} else {
			// check user access
			List<OntologyUserAccess> userPrivilleges = userAccessRepository.findByOntologyIdAndUser(ontology, user);
			if (userPrivilleges.size() > 1) {
				return "ALL";
			} else if (userPrivilleges.size() == 1) {
				return userPrivilleges.get(0).getOntologyUserAccessType().getName();
			}
		}
		throw new NotAllowedException("User " + user.getUserId() + " has no provilleges over the requested ontology "
				+ ontologyIdentification + ".");
	}

}
