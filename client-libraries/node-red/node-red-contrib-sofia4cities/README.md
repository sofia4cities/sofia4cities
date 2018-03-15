# Sofia2 Node-RED

*Read this in other languages: [English](README.md), [Spanish](README.es.md).*

## Updates:

* **December 2017**
	* Components support HTTPS connection 
	* Logs added


## Copyright notice

© 2013-17 Minsait

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## API documentation

Before using the SSAP API for the first time, we strongly recommend that you learn the main concepts of the Sofia2 platform. They have been included in the Sofia2 developer documentation, which can be downloaded from http://sofia2.com/desarrollador_en.html.

The Api source code comes with a test suite where you can see an usage example of every possible query and format

## Repository contents
This repository contains the following nodes for queries on Sofia2 from Node-RED:

* **sofia2-connection-config**: Configuration nodes are scoped globally by default, this means the state will be shared between flows. This node represent a shared connection to a remote system. In that instance, the config node is responsible for creating the connection (REST or MQTT) and making it available to the nodes that use the config node. The following parameters are required to define the connection:
  * Protocol: The protocol may be REST or MQTT. For MQTT connection is necessary to indicate the IP and Port number, whereas for REST connection is only necessary the endpoint.
  * ThinKP and Instance: It is used to indicate the KP Instance to be referenced.
  * Token: Identification numbber of the ThinKP.
  * Renovate session: Connection renewal time.
  
* **sofia2-delete**: This node deletes data from an ontology according to a query, the following parameters are required:
  * Ontology: Name of the ontology.
  * Query: Query to delete.
  * Query Type: query type of the query.

* **sofia2-insert**: This node inserts data in an ontology, it is necessary to indicate the name of the ontology as well as the data to be inserted in JSON format.

* **sofia2-leave**: This node leaves the session, it is necessary to indicate sessionKey that should be closed.

* **sofia2-query**: This node execute a query on an ontology, the following parameters are required:
  * Ontology: Name of the ontology.
  * Query: Query to execute.
  * Query Type: query type of the query.
  
* **sofia2-update**: This node updatee data in an ontology, the following parameters are required:
  * Ontology: Name of the ontology.
  * Query: Query to update.
  * Query Type: query type of the query.

This repository contains also the following directories:

* [lib](lib): this directory contains the files needed to build SSAP messages for the REST connection, SSAP Resource and a KP for the MQTT connection.
* [icons](icons): this directory contains the icons used in the nodes.

## Contact information

If you need support from us, please feel free to contact us at [plataformasofia2@indra.es](mailto:plataformasofia2@indra.es) or at www.sofia2.com.

And if you want to contribute, send us a pull request.
