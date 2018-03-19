SSAP Protocol
============================

The SSAP Protocol (Smart Space Access Protocol) is the standard messaging protocol used in Sofia4Cities IoTPlatform to allow a client or device to communicate with the IoT Broker of the platform to send or consume information.

Using the SSAP protocol, a device can collaborate with any other connected to the platform by exchanging information.

The SSAP protocol is independent of the programming language and communication protocol, allows the devices to communicate with each other, regardless of the language in which they are programmed (Java, C, Android ...) and that Gateway use to connect to the SIB (REST, MQTT, AMQP, ...)

The SSAP protocol is based on JSON, which makes it a lightweight and appropriate protocol for low-resource devices, browsers, mobile phones ...

It is a synchronous protocol (each request has a response) and where all messages start from the client or KP to the SIB, except for subscription notifications that start from the SIB and have no response.

The protocol is composed by these messages:

- **JOIN**: to establish a session between a client and the platform
- **LEAVE**: to finish a established session
- **INSERT/UPDATE/DELETE**: to send a message with an Insert, Update or Delete
- **QUERY**: to make a query to the platform, supported NATIVE and SQL Queries.
- **SUBSCRIBE** and **NOTIFICATION**: to subscribe to changes in an ontology and to send notifications to this client
- **CONFIG**: to send a Config Message to the platforms
- **ERROR**: send a Error Message from the client to the platform

### JOIN
This message is used by a client to establish a logical session. It allows a device to send its authentication credentials to the IoTBroker, to receive or renew a session key with which to send and receive information from the platform.

A JOIN REQUEST is like that:
```json
{  
   "messageId":"",
   "sessionKey":null,
   "direction":"REQUEST",
   "messageType":"JOIN",
   "body":{  
      "@type":"SSAPBodyJoinMessage",
      "token":"1984fd5137e74e26ad1e5163c88c9e60",
      "clientPlatform":"GTKP-Example",
      "clientPlatformInstance":"kp01"
   }
}
```

And a JOIN RESPONSE:
```json
{  
   "messageId":"",
   "sessionKey":"e6eddd9a-6f06-4016-b43d-8b97aeb3acac",
   "direction":"RESPONSE",
   "messageType":"JOIN",
   "body":{  
      "@type":"SSAPBodyReturnMessage",
      "ok":true,
      "error":null,
      "errorCode":null,
      "data":{  
         "sessionKey":"e6eddd9a-6f06-4016-b43d-8b97aeb3acac"
      }
   }
}
```

### INSERT
The INSERT message is used to send information to the platform. It is semantic information classified by its corresponding ontology. Specifically, the information is an instance of ontology, or an insertion sentence. 
The response will be the object identifier assigned in the RealTimeDB to the ontology instance, which will be useful for later updates.

INSERT REQUEST:
```json
{  
   "messageId":"",
   "sessionKey":"2ede3e6b-6d96-4cdf-9842-04393d37e6c0",
   "direction":"REQUEST",
   "messageType":"INSERT",
   "body":{  
      "@type":"SSAPBodyInsertMessage",
      "ontology":"testClientjs",
      "data":{  
         "EmptyBase":{  
            "test":"testClientValue"
         }
      }
   }
}
```

INSERT RESPONSE:
```json
{  
   "messageId":"",
   "sessionKey":"2ede3e6b-6d96-4cdf-9842-04393d37e6c0",
   "direction":"RESPONSE",
   "messageType":"INSERT",
   "body":{  
      "@type":"SSAPBodyReturnMessage",
      "ok":true,
      "error":null,
      "errorCode":null,
      "data":{  
         "id":"5aa81ef281222e3624832b63"
      }
   }
}
```

### QUERY

The QUERY message is used to consult existing information on the platform. It allows to send to the platform a sentence to execute in the RealTimeDB or HistoricalDB BDH to receive information inserted by any client. (Provided they have the appropriate rights).

QUERY REQUEST
```json
{  
   "messageId":"",
   "sessionKey":"2ede3e6b-6d96-4cdf-9842-04393d37e6c0",
   "direction":"REQUEST",
   "messageType":"QUERY",
   "body":{  
      "@type":"SSAPBodyQueryMessage",
      "ontology":"MyOntology",
      "query":"select * from MyOntology",
      "queryType":"SQL"
   }
}
```
