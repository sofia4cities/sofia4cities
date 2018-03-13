<p align="center">
  <a src='https://www.sofia4cities.com/'>
    <img src='s4c.png'/>
  </a>
</p>

============================
To use this library add following dependency to your proyect.
<dependency>
	<groupId>com.indracompany.sofia2</groupId>
	<artifactId>sofia2-java-clients</artifactId>
	<version>${sofia2.version}</version>
</dependency>

## MQTT

MQTTClient allows your application to connect and send data to the IoT Broker, using MQTT as the transport protocol.

To use this client:
	- Import com.indracompany.sofia2.client.MQTTClient;
	- Create new client, specifying URI of the MQTT server, ex. new MQTTClient("tcp://localhost:1883");
	- To connect client to Broker, you need to call method "connect()" with aguments: token, device identification, device instance identification, and timeout for waiting broker response (in seconds). ex. client.connect("3f77b9a6af8642c683e5d786217afb0b", "RestaurantsCp", "RestaurantsCp:mqtt", 5); 
	- You can publish messages through method "publish()", arguments needed: ontology identification name, ontology instance as string (must be json format!), and timeout in seconds.
	- To terminate the connection, call method "disconnect()" ex. client.disconnect();