Javascript Client Library:
============================


## Use the library:

The library has the following dependencies:

	- jQuery (you can download from https://code.jquery.com/jquery-3.3.1.min.js)
	- stomp (you can download fromhttps://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.js)
	- sockjs (you can download fromhttps://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.js)


## Example

To use this client:

- Create a config object to call the config method. Create the config object with the following aguments: url, device identification, device instance identification. 
  
  Example: {"url":"http://localhost:8081/iotbroker/message",
			"token":"1984fd5137e74e26ad1e5163c88c9e60", 
			"clientPlatform": "GTKP-Example", 
			"clientPlatformInstance":"kp01"}
	
- To connect client to Broker, you need to call method "connect()"
		
- Now you can call all the allowed methods (join, query, insert, update, updateById, remove, removeById)
