module.exports = function(RED) {
	var ssapMessageGenerator = require('../lib/SSAPMessageGenerator');
	var sofia2Config = require('../sofia2-connection-config/sofia2-connection-config');
	var ssapResourceGenerator = require('../lib/SSAPResourceGenerator');
	var http = null;
	var isHttps = false;
	
    function Update(n) {
        RED.nodes.createNode(this,n);
        
		var node = this;
		this.ontology = n.ontology;
		this.query = n.query;
		this.queryType = n.queryType;
		
		// Retrieve the server (config) node
		var server = RED.nodes.getNode(n.server);
        
		this.on('input', function(msg) {
			var ontologia="";
			var queryType="";
			var query="";
			if(this.ontology==""){
			   ontologia = msg.ontology;
			}else{
			   ontologia=this.ontology;
			}
			if(this.queryType==""){
			   queryType = msg.queryType;
			}else{
			   queryType=this.queryType;
			}
			if(this.query==""){
			   query = msg.query;
			}else{
			   query=this.query;
			}
			if (server) {
				var protocol = server.protocol;
				if(protocol.toUpperCase() == "MQTT".toUpperCase()){
					var queryUpdate = ssapMessageGenerator.generateUpdateWithQueryTypeMessage(msg.payload,query, ontologia,queryType,server.sessionKey);
				
					var state = server.sendToSib(queryUpdate);
					
					state.then(function(response){
						
						var body = JSON.parse(response.body);
						if(body.ok){
							console.log("The message is send.");
							msg.payload=body;
							node.send(msg);
						}else{
							console.log("Error sendind the update SSAP message.");
							msg.payload=body.error;
							if(body.errorCode == "AUTENTICATION"){
								console.log("The sessionKey is not valid.");
								server.generateSession();
							}
							node.send(msg);
						}
					});
				}else if(protocol.toUpperCase() == "REST".toUpperCase()){
					query = query.replace(/ /g, "+");
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
					
					//Se prepara el mensaje update
							
					var queryUpdate='?$sessionKey='+server.sessionKey+'&$query='+query+'&$queryType='+queryType;
					
					var postheadersUpdate = {
						'Content-Type' : 'application/json',
						'Accept' : 'application/json',
					};
					
					var optionsUpdate = {
					  host: host,
					  port: port,
					  path: '/sib/services/api_ssap/v01/SSAPResource/'+queryUpdate,
					  method: 'GET',
					  headers: postheadersUpdate,
					  rejectUnauthorized: false
					};
					// do the UPDATE GET call
					var resultUpdate='';
					if (isHttps) 
						http= require('https');
					else
						http = require('http');
					var reqUpdate = http.request(optionsUpdate, function(res) {
						console.log("Status code of the Update call: ", res.statusCode);
						res.on('data', function(d) {
							resultUpdate +=d;
						});
						res.on('end', function() {
							if(res.statusCode==200){
								console.log("The data has been updated.");
								try{
									resultUpdate = JSON.parse(resultUpdate);
									msg.payload=resultUpdate;
								} catch (err) {
									msg.payload=resultUpdate;
								}
								node.send(msg);
							}else if(res.statusCode==400 || res.statusCode==401){
								var instance = server.kp + ':' + server.instance;
								var queryJoin = ssapResourceGenerator.generateJoinByTokenMessage(server.kp, instance, server.token);
								
								var postheadersJoin = {
									'Content-Type' : 'application/json',
									'Accept' : 'application/json',
									'Content-Length' : Buffer.byteLength(queryJoin, 'utf8')
								};
								
								var optionsJoin = {
								  host: host,
								  port: port,
								  path: '/sib/services/api_ssap/v01/SSAPResource/',
								  method: 'POST',
								  headers: postheadersJoin,
								  rejectUnauthorized: false
								};
								
								// do the JOIN POST call
								var result='';
								var reqPost = http.request(optionsJoin, function(res) {
									console.log("Status code of the Join call: ", res.statusCode);
									res.on('data', function(d) {
										result +=d;
									});
									res.on('end', function() {
										result = JSON.parse(result);
										server.sessionKey=result.sessionKey;
										console.log("SessionKey obtained: " + server.sessionKey);
										//Se prepara el mensaje update
										
										var queryUpdate='?$sessionKey='+server.sessionKey+'&$query='+query+'&$queryType='+queryType;
										
										var postheadersUpdate = {
											'Content-Type' : 'application/json',
											'Accept' : 'application/json',
										};
										
										var optionsUpdate = {
										  host: host,
										  port: port,
										  path: '/sib/services/api_ssap/v01/SSAPResource/'+queryUpdate,
										  method: 'GET',
										  headers: postheadersUpdate,
										  rejectUnauthorized: false
										};
										// do the UPDATE GET call
										var resultUpdate='';
										var reqUpdate = http.request(optionsUpdate, function(res) {
											console.log("Status code of the Update call: ", res.statusCode);
											res.on('data', function(d) {
												resultUpdate +=d;
											});
											res.on('end', function() {
												if(res.statusCode=="200"){
													console.log("The data has been updated.");
													try{
														resultUpdate = JSON.parse(resultUpdate);
														msg.payload=resultUpdate;
													} catch (err) {
														msg.payload=resultUpdate;
													}
													node.send(msg);
												}
											});
											
										});
										reqUpdate.end();
										reqUpdate.on('error', function(err) {
											console.log("There was an error updating the data: ", err);
										});
									});
									
								});
								reqPost.write(queryJoin);
								reqPost.end();
								reqPost.on('error', function(err) {
									console.log("Error:"+err);
									node.error("Error:"+err);
								});
										}
									});
									
								});
								reqUpdate.end();
								reqUpdate.on('error', function(err) {
									console.log("There was an error updating the data: ", err);
								});
				}
				
			} else {
				console.log("Error:"+err);
				node.error("Error:"+err);
			}
        });
		
    }
    RED.nodes.registerType("sofia2-update",Update);
}