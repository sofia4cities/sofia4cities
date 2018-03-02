
var bodyParser = require('body-parser');
var http = require('http');
var express = require("express");
var Multimap = require('multimap');

;
var typeNodeSSAP = "ssap-process-request";
var typeNodeRules = "script-topic";

//Event Listeners, one for each Sofia2 Node Type
var ssapProcessRequestEventListeners = new Array();
var notifyRulesEventListeners = new Multimap();

//Creates the server to receive events from Sofia2, It will start the server when the service port is received
// Create an Express app
var app = express();

//Prepares de app to receive JSON
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());


// Add a simple route for static content served from 'public'
app.use("/",express.static("public"));

// Create a server
var serverSofia2Listener = http.createServer(app);



// Create a POST endpoint
app.post('/notification', function(request, response){
 
	var body = request.body;
	
	if(body.target){
		switch (body.target.toString()) {
			case typeNodeSSAP:

				notifyListeners(ssapProcessRequestEventListeners, body);	
				break;  
			case typeNodeRules:
				var topic = body.topic.toString();
				notifyListenersRules(notifyRulesEventListeners.get(topic), body);
			break;
			default: 
				console.log("Destino no valido");
		}
	}else{
		console.log("No se ha podido determinar el destino del mensaje");
		console.log(request.body);

	}
	
	response.status(200).end();

 });
 
 

/*
 * Filtra los Nodos de tipo SSAP antes del envío de mensajes
 *
 */
function filterNodeSSAP(node, body) {

    if ((node != null && node != undefined) && (body != null && body != undefined)) {
	  
        var nodeType = node.type;
        var nodeDirection = node.direccion;
        var nodeTypeMsg = node.tipomensaje;
        var nodeOntology = node.ontology;
        var nodeKP = node.kp;
        var nodeInstanceKP = node.instanciakp;
        
        
        var bodyType = body.type;
        var bodyDirection = body.directionNode;
        var bodyTypeMsg = body.type;
        var bodyOntology = body.ontology;
        var bodyKP = body.kp;
        var boydInstanceKP = body.instanceKp;
        

        if(nodeDirection!=null && nodeDirection !=undefined && bodyDirection!=null && bodyDirection!=undefined){
            if(nodeDirection.toString().toUpperCase() != bodyDirection.toString().toUpperCase()){
            	return false;
            }  

        }else if(nodeTypeMsg!=null && nodeTypeMsg!=undefined && nodeTypeMsg.toString().toUpperCase()!='ALL' && bodyTypeMsg!=null && bodyTypeMsg!=undefined){
        	if(nodeTypeMsg.toString().toUpperCase()!=bodyTypeMsg.toString().toUpperCase()){
        		return false;
        	}
        }else if(nodeOntology!=null && nodeOntology!=undefined && nodeOntology.toString().toUpperCase()!='' && bodyOntology!=null && bodyOntology!=undefined){
        	if(nodeOntology.toString().toUpperCase()!=bodyOntology.toString().toUpperCase()){
        		return false;
        	}
        }else if(nodeKP!=null && nodeKP!=undefined && nodeKP.toString().toUpperCase()!='' && bodyKP!=null && bodyKP != undefined){
        	if(nodeKP.toString().toUpperCase()!=bodyKP.toString().toUpperCase()){
        		return false;
        	}
        }else if(nodeInstanceKP !=null && nodeInstanceKP!=undefined && nodeInstanceKP.toString().toUpperCase()!='' && bodyInstanceKP!=null && bodyInstanceKP!=undefined){
        	if(nodeInstanceKP.toString().toUpperCase()!=bodyInstanceKP.toString().toUpperCase()){
        		return false;
        	}
        }


    }
  
    return true;
}

function filterNodeRules(node, body) {

    if ((node != null && node != undefined) && (body != null && body != undefined)) {
	  
        var nodeType = node.type;
        var nodeTopic = node.topic;
        
        var bodyType = body.type;
        var bodyTopic = body.topic;

        if(nodeTopic!=null && nodeTopic !=undefined && bodyTopic!=null && bodyTopic!=undefined){
            if(nodeTopic.toString().toUpperCase() != bodyTopic.toString().toUpperCase()){
            	return false;
            }  
        }else if(nodeType!=null && nodeType !=undefined && bodyType!=null && bodyType!=undefined){
            if(nodeType.toString().toUpperCase() != bodyType.toString().toUpperCase()){
            	return false;
            }  
        }
    }
  
    return true;
}


function notifyListeners(listeners,body) {
  
  for (i in listeners){
    var node = listeners[i];
    if(node!=null && node !=undefined && node.type ==typeNodeSSAP){
         //filtramos
         if(filterNodeSSAP(node,body)){

            listeners[i].emit('notifySofia2Event',body.message);
         }

    }
  }

  
}

  function notifyListenersRules(listeners,body) {
  
	listeners.forEach(function(listener, index) {
		
	  var node = listener;
	  
	  if(node!=null && node!=undefined){
		  
		  if(node.type == typeNodeRules){
				 
			 if(filterNodeRules(node,body)){
			   listener.emit('notifySofia2Event', body.data);              
			 }
		  }
	  }
  });

  
}


module.exports = {
	init: function(servicePort) {
        //Start the server
		console.log("Start server to listen Sofia2 events");
		serverSofia2Listener.listen(servicePort);
    },
	registerSsapProcessRequestEventListeners: function(listener){//Hay que enganchar el registro de listeners
 
		  ssapProcessRequestEventListeners.push(listener);
		
	},
	deRegisterSsapProcessRequestEventListeners: function(listener){//Y el de-registro de listeners
		
    var index = ssapProcessRequestEventListeners.indexOf(listener);
		
		//existen elementos en el array.
		if (index > -1) {
			console.log("Delete nodes types SSAP");
            ssapProcessRequestEventListeners = [];
		}
    
    
	},
	registerNotifyRulesEventListeners: function(listener, topic){//Hay que enganchar el registro de listeners
		
		notifyRulesEventListeners.set(topic,listener);
	},
	deRegisterNotifyRulesEventListeners: function(listener, topic){//Y el de-registro de listeners
		
		notifyRulesEventListeners.delete(topic, listener);
	},
	stop: function(){
		serverSofia2Listener.close();
	}
}

