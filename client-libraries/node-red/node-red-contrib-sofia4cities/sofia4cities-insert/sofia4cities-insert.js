module.exports = function(RED) {
	var ssapMessageGenerator = require('../lib/SSAPMessageGenerator');
	var kp = require('../lib/kpMQTT');
	var ssapResourceGenerator = require('../lib/SSAPResourceGenerator');
	var http = null;
	var isHttps = false;

    function Insert(n) {
        RED.nodes.createNode(this,n);
        var node = this;
		this.ontology = n.ontology;

		// Retrieve the server (config) node
		 var server = RED.nodes.getNode(n.server);


		this.on('input', function(msg) {
			var ontologia="";
			var sessionKey='';
			if(this.ontology==""){
			   ontologia = msg.ontology;
			}else{
			   ontologia=this.ontology;
			}

			if (server) {
				var protocol = server.protocol;
				console.log("Using protocol:"+protocol);
				console.log("Using ontology:"+ontologia);

				if(protocol.toUpperCase() == "MQTT".toUpperCase()){
					if (server.sessionKey==null || server.sessionKey=="")			 {
						server.generateSession();
					    console.log("SessionKey null...Generated SessionKey:"+server.sessionKey);
					}
				  console.log("Using SessionKey:"+server.sessionKey);

					var queryInsert = ssapMessageGenerator.generateInsertMessage(msg.payload, ontologia,server.sessionKey);
					console.log("Using query:"+queryInsert);

					var state = server.sendToSib(queryInsert);
					if(typeof(state)=="undefined" || state==""){
						console.log("There are not response for the query send.");
					}else{
						state.then(function(response){
							var body = response.body;
							console.log("Responde Body:"+body);
							if(body.ok){
								console.log("Message sent OK. Body:"+body);
								msg.payload=body;
								node.send(msg);
							}else{
								console.log("Error sending SSAP message:"+body.error);
								msg.payload=body.error;
								if(body.errorCode == "AUTENTICATION"){
									console.log("The sessionKey is not valid.");
									server.generateSession();
								}
								node.send(msg);
							}
						});
					}
				}else if(protocol.toUpperCase() == "REST".toUpperCase()){
					var endpoint = server.endpoint;
					var arr = endpoint.toString().split(":");

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

					//Se prepara el mensaje insert
					var queryInsert = ssapResourceGenerator.generateInsertMessage(msg.payload, ontologia, server.sessionKey);
					console.log("Query Insert:"+queryInsert);

					var postheadersInsert = {
						'Content-Type' : 'application/json',
						'Accept' : 'application/json',
						'Content-Length' : Buffer.byteLength(queryInsert, 'utf8')
					};
					var path = "/sib/services/api_ssap/v01/SSAPResource/";

					console.log("URL invocation:"+host+":"+port+path);
					var optionsInsert = {
					  host: host,
					  port: port,
					  path: path,
					  method: 'POST',
					  headers: postheadersInsert,
					  rejectUnauthorized: false
					};
					// do the INSERT POST call
					var resultInsert='';
					if (isHttps)
						http= require('https');
					else
						http = require('http');
					var reqInsert = http.request(optionsInsert, function(res) {
						console.log("Status code of the Insert call: ", res.statusCode);
						res.on('data', function(d) {
							resultInsert +=d;
						});
						res.on('end', function() {
							if(res.statusCode==500 ||res.statusCode==400 || res.statusCode==401){
								//La sessionKey no es válida, se hace el join
								//Se regenera la sessionKey con un join
								var instance = server.kp + ':' + server.instance;
								var queryJoin = ssapResourceGenerator.generateJoinByTokenMessage(server.kp, instance, server.token);
								console.log("queryJoin: ",queryJoin);

								var postheadersJoin = {
									'Content-Type' : 'application/json',
									'Accept' : 'application/json',
									'Content-Length' : Buffer.byteLength(queryJoin, 'utf8')
								};

								var optionsJoin = {
								  host: host,
								  port: port,
								  path: path,
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
										var queryInsert = ssapResourceGenerator.generateInsertMessage(msg.payload, ontologia, server.sessionKey);
										var postheadersInsert = {
											'Content-Type' : 'application/json',
											'Accept' : 'application/json',
											'Content-Length' : Buffer.byteLength(queryInsert, 'utf8')
										};

										var optionsInsert = {
										  host: host,
										  port: port,
										  path: '/sib/services/api_ssap/v01/SSAPResource/',
										  method: 'POST',
										  headers: postheadersInsert,
										  rejectUnauthorized: false
										};

										var resultInsert='';
										var reqInsert = http.request(optionsInsert, function(res) {
											console.log("Status code of the Insert call: ", res.statusCode);
											res.on('data', function(d) {
												resultInsert +=d;
											});
											res.on('end', function() {
												try{
													resultInsert = JSON.parse(resultInsert);
													msg.payload=resultInsert;
												} catch (err) {
													msg.payload=resultInsert;
													console.log("Error:"+err);
													node.error("Error:"+err);
												}
												node.send(msg);
											});

										});
										reqInsert.write(queryInsert);
										reqInsert.end();
										reqInsert.on('error', function(err) {
													console.log("Error:"+err);
													node.error("Error:"+err);
										});
									});

								});
								reqPost.write(queryJoin);
								reqPost.end();
								reqPost.on('error', function(err) {
									console.log("There was an error inserting the data: ", err);
									console.log("Error:"+err);
									node.error("Error:"+err);
								});

							}else if(res.statusCode==200){
								try{
									resultInsert = JSON.parse(resultInsert);
									msg.payload=resultInsert;
								} catch (err) {
									msg.payload=resultInsert;
									console.log("Error:"+err);
									node.error("Error:"+err);
								}
								node.send(msg);
							}
						});

					});
					reqInsert.write(queryInsert);
					reqInsert.end();
					reqInsert.on('error', function(err) {
						console.log(err);
						console.log("Error:"+err);
						node.error("Error:"+err);
					});
				}

			} else {
				console.log("Error");
			}

        });

    }
    RED.nodes.registerType("sofia4cities-insert",Insert);
}
