module.exports = function(RED) {
	var ssapMessageGenerator = require('../lib/SSAPMessageGenerator');
	var sofia2Config = require('../sofia2-connection-config/sofia2-connection-config');
	var ssapResourceGenerator = require('../lib/SSAPResourceGenerator');

	var http = null;
	var isHttps = false;
	
    function Delete(n) {
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
					var queryDelete = ssapMessageGenerator.generateDeleteWithQueryTypeMessage(query, ontologia,queryType,server.sessionKey);
				
					var state = server.sendToSib(queryDelete);
					
					state.then(function(response){
						
						var body = JSON.parse(response.body);
						if(body.ok){
							console.log("The message is send.");
							msg.payload=body;
							node.send(msg);
						}else{
							console.log("Error sendind the delete SSAP message.");
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
					var instance = server.kp + ':' + server.instance;
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
					
					//Se prepara el mensaje delete
					var queryDelete='?$sessionKey='+server.sessionKey+'&$query='+query+'&$queryType='+queryType;
					queryDelete = queryDelete.replace(/ /g, "+");

					var postheadersDelete = {
						'Content-Type' : 'application/json',
						'Accept' : 'application/json',
						//'Content-Length' : Buffer.byteLength(queryDelete, 'utf8')
					};
					
				
					var optionsDelete = {
					  host: host,
					  port: port,
					  path: '/sib/services/api_ssap/v01/SSAPResource'+queryDelete,
					  method: 'GET',
					  headers: postheadersDelete,
					  rejectUnauthorized: false
					};
					// do the GET POST call
					var resultDelete='';
					if (isHttps) 
						http= require('https');
					else
						http = require('http');
					var reqDelete = http.request(optionsDelete, function(res) {
						console.log("Status code of the Delete call: ", res.statusCode);
						res.on('data', function(d) {
							resultDelete +=d;
						});
						res.on('end', function() {
							if(res.statusCode==400 || res.statusCode==401){
								//La sessionKey no es válida, se hace el join
								//Se regenera la sessionKey con un join
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
										
										//Se prepara el mensaje delete
										var queryDelete='?$sessionKey='+server.sessionKey+'&$query='+query+'&$queryType='+queryType;
										queryDelete = queryDelete.replace(/ /g, "+");

										var postheadersDelete = {
											'Content-Type' : 'application/json',
											'Accept' : 'application/json',
											//'Content-Length' : Buffer.byteLength(queryDelete, 'utf8')
										};
										
										var optionsDelete = {
										  host: host,
										  port: port,
										  path: '/sib/services/api_ssap/v01/SSAPResource'+queryDelete,
										  method: 'GET',
										  headers: postheadersDelete,
										  rejectUnauthorized: false
										};
										
										// do the GET POST call
										var resultDelete='';
										var reqDelete = http.request(optionsDelete, function(res) {
											console.log("Status code of the Delete call: ", res.statusCode);
											res.on('data', function(d) {
												resultDelete +=d;
											});
											res.on('end', function() {
												if(res.statusCode=="200"){
													console.log("The data has been deleted.");
													try{
														resultDelete = JSON.parse(resultDelete);
														msg.payload=resultDelete;
													} catch (err) {
														msg.payload=resultDelete;
													}
													node.send(msg);
												}
											});
											
										});
										reqDelete.end();
										reqDelete.on('error', function(err) {
											console.log("Error:"+err);
											node.error("Error:"+err);
										});
										
										
									});
									
								});
								reqPost.write(queryJoin);
								reqPost.end();
								reqPost.on('error', function(err) {
									console.log("There was an error inserting the data: ", err);
								});
								
							}else if(res.statusCode==200){
								try{
									resultDelete = JSON.parse(resultDelete);
									msg.payload=resultDelete;
								} catch (err) {
									msg.payload=resultDelete;
								}
								node.send(msg);
							}
						});
						
					});
					//reqDelete.write(queryDelete);
					reqDelete.end();
					reqDelete.on('error', function(err) {
									console.log("Error:"+err);
									node.error("Error:"+err);
					});
				}
					
			} else {
				console.log("Error");
			}
        });
		
    }
    RED.nodes.registerType("sofia2-delete",Delete);
}