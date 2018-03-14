module.exports = function(RED) {
	"use strict";
	var kp = require('../lib/kpMQTT');
	var ssapMessageGenerator = require('../lib/SSAPMessageGenerator')
	var waitUntil = require('wait-until');

	//Invoca al constructor al desplegar el flujo
    function SofiaConfig(n) {

        RED.nodes.createNode(this,n);
	this.on('close', function () {
		console.log('Closing MQTT connection...');
		myKp.disconnect();
		console.log("Connection closed.");
	});

    this.protocol=n.protocol;
		this.kp=n.kp;
		this.instance=n.instance;
		this.token=n.token;
		this.renovation=n.renovation;

		var node=this;
		node.sessionKey="";
		node.connected=false;

		var myKp;

		//Para detener el intervalo de renovación de sesión
		var testConnectionInterval;



		if(this.protocol.toUpperCase() === "MQTT".toUpperCase()){
			this.ip=n.ip;
			this.port=n.port;
			console.log(this.ip + ":" + this.port);
			myKp = new kp.KpMQTT();

			console.log("MQTT: Trying to connect on: "+this.ip+":"+this.port);

			//Connect to the SIB
			myKp.connect(this.ip, this.port);

			//Chequea 5 veces con un intervarlo de x segundos si se ha conectado (En definitiva 5 segundos que es el connection timeout del kpMQTT)
			waitUntil(1000, 5,
						function condition() {
							return myKp.isConnected();
						},
						function done(result) {
							if(result){//Está conectado
								//Generate Sessionkey
								generateSession();
							}else{
								console.log("No se ha podido conectar");
							}
						}
			);

			testConnectionInterval= setInterval( function() {
				if (myKp != null && typeof(myKp) != "undefined")  {
					if (!myKp.isConnected()) {
						node.log("Physic reconnection");
						myKp.connect(node.ip, node.port);
						waitUntil(1000, 5,
							function condition() {
								return myKp.isConnected();
							},
							function done(result) {
								if(result){//Está conectado
									node.sessionKey=="";
									generateSession();
								}else{
									console.log("Error on Connection by: "+result);
								}
							}
						);

					}else{
						node.log("KP Connected. Renew Sessionkey");
						generateSession();
					}
				}
			}, this.renovation * 60000);	// retry every renovation minutes


		}else{

			this.endpoint=n.endpoint;

		}


		function generateSession () {
			console.log("The sessionKey is going to be generated...")
			if(typeof(myKp) != "undefined"){
				var ssapMessageJOIN;

				if( typeof(node.sessionKey)=="undefined" || node.sessionKey==""){
					console.log("There is no previous session, generate new session...")
					ssapMessageJOIN = ssapMessageGenerator.generateJoinByTokenMessage(node.token, node.kp, node.instance );

				}else{ //There is a previouse session. Try to renovate it
					console.log("There is a previouse session. Try to renovate it...")
					console.log(node.token);
					console.log(node.kp);
					console.log(node.instance);
					console.log(node.sessionKey);
					ssapMessageJOIN = ssapMessageGenerator.generateJoinRenovateByTokenMessage(node.token, node.kp, node.instance, node.sessionKey );
				}
				console.log(ssapMessageJOIN);
				myKp.send(ssapMessageJOIN)
					.then(function(joinResponse) {
						console.log('Response body: ' + JSON.stringify(joinResponse));
						if (joinResponse.sessionKey !== null) {
							node.sessionKey = joinResponse.sessionKey;
							node.connected = true;
							console.log('Session created with iotBroker with sessionKey: ' + node.sessionKey);
						} else {//Sobre todo renovación de sesión
							//check exception management etc.

							node.connected = false;
							node.sessionKey="";
						}
					})
					.done(function() {
						console.log('Connection established with SessionKey:'+node.sessionKey);
					});
			}

		}

		function setNotification(func, subscribeId){
			myKp.setNotificationCallback(func, subscribeId);
		}
		node.setNotification=setNotification;
		node.generateSession=generateSession;

		//Se invoca al cerrar y al redesplegar el flujo
		this.on('close', function() {
			clearInterval(testConnectionInterval);

			//LEAVE  y un disconnect físico
			var queryLeave = ssapMessageGenerator.generateLeaveMessage(node.sessionKey);
			if(this.server=="undefined"){
				console.log("server: " + this.server);
				var state = this.server.sendToSib(queryLeave);

				state.then(function(response){

					var body = JSON.parse(response.body);
					if(body.ok){
						console.log("The message is send.");
						myKp.disconnect();
					}else{
						console.log("Error sendind the leave SSAP message.");
						if(body.errorCode == "AUTHENTICATION"){
							console.log("The sessionKey is not valid.");
							generateSession();
						}
					}
				});
			}
		});


		//Envia un mensaje al SIB
		this.sendToSib=function(msg) {
		  if(typeof(myKp) != "undefined"){
			return myKp.send(msg);
		  }
		}
		//Devuelve la sessionkey de la conexión
		this.getSessionKey=function() {
		  return node.sessionKey;
		}
    }

    RED.nodes.registerType("sofia2-config",SofiaConfig);
}
