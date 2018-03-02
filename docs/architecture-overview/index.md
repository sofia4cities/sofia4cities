This section shows fundamental concepts behind Sofia4Cities architecture and logical design.

Sofia4Cities IoT platform consists of the following modules:

- IoT Broker
- API Manager
- Router Engine
- Flow Engine
- Scripting Engine
- Control Panel
- Monitoring UI

These modules uses others components of the platform:
- Config Components
- Persistence Components
- Security Components
- Communication Components

## Components Diagram for Management Flow
![Management Flow](management_diagram.txt.png) 
This diagram show the main components involved in the management of the platform.

## Components Diagram for IoT Flow
![Management Flow](iot_diagram.txt.png) 
This diagram show the main components involved in the IoT Flow of the platform.


## IoT Broker
Sofia4Cities IoTBroker is the module that ...
In the reference implementations supports MQTT, REST and Web Sockets protocols.
Devices can communicate with the IoT Broker using one of the CommunicationAPIs developed.
We offer a Java API, Python API and Node-red API. You can use REST Communication with other languages and even you can develop you own Communication API.

### High Availability & Horizontal Scaling

To support **high availability (HA)**, a IoT cluster must include at least two nodes. 
For the purpose of horizontal scaling, you can set up a Sofia4Cities cluster 
In this case, all instances will function concurrently.
IoTBroker can re-balance the load at run time, thus effectively routing endpoints to the less loaded nodes in the cluster.

## API Manager

The API Manager is in charge of ...


## Third-party components

The platform use and is based in these components.
- Eclipse Moquette as embedded MQTT Broker
- JSON-Schema as reference implementation for ontologies
- NGINX as default HTTP balancer and HAProxy as TCP/MQTT balancer
- Spring as glue framework for all development
- Spring Boot for development and deployment
- Thymeleaf for Control Panel UI development
- Hazelcast for internode communication
- MongoDB as default RealTimeDB (although you can use another databases as Cassandra, HBase, MemSQL,...)
- SQL Database for storing all configuration of the platform
- Docker for containerization
- ...


## Further reading

Use the following guides and references to learn more about Sofia4Cities features.

