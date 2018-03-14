module.exports = function(RED) {
	var ssapMessageGenerator = require('../lib/SSAPMessageGenerator');
	var sofia2Config = require('../sofia2-connection-config/sofia2-connection-config');
	var ssapResourceGenerator = require('../lib/SSAPResourceGenerator');
	var http = null;
	var isHttps = false;
	
    function Leave(n) {
        RED.nodes.createNode(this,n);
        var node = this;
		
		// Retrieve the server (config) node
		var server = RED.nodes.getNode(n.server);
		
		this.on('input', function(msg) {
			
			if (server) {
				var protocol = server.protocol;
				if(protocol.toUpperCase() == "MQTT".toUpperCase()){
					var queryLeave = ssapMessageGenerator.generateLeaveMessage(server.sessionKey);
				
					var state = server.sendToSib(queryLeave);
					
					state.then(function(response){
						
						var body = JSON.parse(response.body);
						if(body.ok){
							console.log("The message is send.");
							msg.payload=body;
							node.send(msg);
						}else{
							console.log("Error sendind the leave SSAP message.");
							msg.payload=body.error;
							if(body.errorCode == "AUTENTICATION"){
								console.log("The sessionKey is not valid.");
								server.generateSession();
							}
							node.send(msg);
						}
					});
				}else if(protocol.toUpperCase() == "REST".toUpperCase()){
					var instance = server.kp + ':' + server.instance;
					console.log(instance);
					console.log(server.endpoint);
					var endpoint = server.endpoint;
					var arr = endpoint.toString().split(":");
					
					var host;
					var port = 80;
					
					if (arr[0].toUpperCase()=='HTTPS'.toUpperCase()) {
						isHttps=true;
						console.log("Using HTTPS:"+arr[0]);
					}
					if(arr[0].toUpperCase()=="HTTP".toUpperCase()||arr[0].toUpperCase()=='HTTPS'.toUpperCase()){
						host=arr[1].substring(2, arr[1].length);
						if(arr.length>2){
							port = parseInt(arr[arr.length-1]);
						}
					}else{
						host = arr[0];	
						if(arr.length>1){
							port = parseInt(arr[arr.length-1]);
						}
					}
					
					var queryLeave = ssapResourceGenerator.generateLeaveMessage(server.sessionKey);
					console.log(queryLeave);
					
					var postheadersLeave = {
								'Content-Type' : 'application/json',
								'Accept' : 'application/json',
								'Content-Length' : Buffer.byteLength(queryLeave, 'utf8')
					};
							
					var optionsLeave = {
					  host: host,
					  port: port,
					  path: '/sib/services/api_ssap/v01/SSAPResource/',
					  method: 'POST',
					  headers: postheadersLeave,
					  rejectUnauthorized: false
					};
					// do the LEAVE POST call
					var resultLeave='';
					if (isHttps) 
						http= require('https');
					else
						http = require('http');
					var reqLeave = http.request(optionsLeave, function(res) {
						console.log("Status code of the Leave call: ", res.statusCode);
						res.on('data', function(d) {
							resultLeave +=d;
						});
						res.on('end', function() {
							if(res.statusCode=="200"){
								console.log("The session is closed.");
							}
						});
						
					});
					reqLeave.write(queryLeave);
					reqLeave.end();
					reqLeave.on('error', function(err) {
						console.log("Error:"+err);
						node.error("Error:"+err);
					});
				}else if(protocol.toUpperCase() == "WEBSOCKET".toUpperCase()){
						console.log("TODO");
				}
				
				
			} else {
					console.log("Error:"+err);
					node.error("Error:"+err);
			}
			
        });
		
    }
    RED.nodes.registerType("sofia2-leave",Leave);
}