Sofia4Cities Java Client Library
============================

## Compile and use the library:

To use this library you need to:
1. Compile the platform, this generates en your local Maven Repository: \com\indracompany\sofia2\sofia2-ssap\5.0.0-SNAPSHOT\sofia2-ssap-5.0.0-SNAPSHOT.jar
2. Compile the client library, this generates \com\indracompany\sofia2\sofia2-java-clients\5.0.0-SNAPSHOT\sofia2-java-clients-5.0.0-SNAPSHOT.jar
3.   Add following dependency to your project:
```
<dependency>
	<groupId>com.indracompany.sofia2</groupId>
	<artifactId>sofia2-java-clients</artifactId>
	<version>${sofia2.version}</version>
</dependency>
```

4. 	Import com.indracompany.sofia2.client.MQTTClient in your java class.


## MQTTS Client API

MQTTClient API allows your application to connect and send data to the IoT Broker, using MQTT as the transport protocol.

## MQTTS (SSL) 

This client allows you to configure MQTT over SSL. To create a secure connection, follow these steps:

-	Create a MQTTSecureConfiguration object specifying the path to your Key Store (.jks), and its password. If one of them is not specified, then default Key Store and password will be used.

-	Create a MQTTClient with the Broker URL and the MQTTSecureConfiguration object.


![](./exampleSSL.png) 


## MQTT not secure 

If you don't want to use a secure connection, you may use standard MQTT protocol:


![](./exampleNotSSL.png) 


## Connect the client

-  To connect client to Broker, you need to call method "connect()" with aguments: token, device identification, device instance identification, and timeout for waiting broker response (in seconds). 
   example: client.connect("token", "Device", "Device:mqtt", 5); 

![](./exampleConnect.png) 


## Publish

-  You can publish messages through method "publish()", arguments needed: ontology identification name, ontology instance as string (must be json format!), and timeout in seconds.


![](./examplePub.png) 


## Subscribe

-	You can subscribe to an ontology through method "subscribe()", by specifying the Query (filter), the ontology identification, the type of query (Native or SQL), timeout in seconds for waiting subscription ACK response, and lastly, a handler/listener is needed for handling incoming messages from the subscription. 

-	By calling this method you get a String with the subscriptionId, needed to unsubscribe.


![](./exampleSubs.png) 


## Subscribe

-	You can unsubscribe with method "unsubscribe()", with the subscriptionId obtained before.



## Disconnect

-  To terminate the connection, call method "disconnect()".



## Using executable example provided (.jar)

A jar is provided to test this client. You can use it by executing "java -jar java-client.jar" with arguments "brokerURI", and optional "key store path" and "password" .


![](./exampleExec.png) 