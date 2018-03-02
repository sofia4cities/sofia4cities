var http = require('http');
var express = require("express");
var RED = require("node-red-sofia");


// Create an Express app
var app = express();

// Add a simple route for static content served from 'public'
app.use("/",express.static("public"));



// Create the settings object - see default settings.js file for other options

var domain = process.env.domain;
var port = process.env.port;
var home = process.env.home;
var servicePort = process.env.servicePort;

 //settings NODE-RED
	 settings = {
	    httpAdminRoot:"/"+domain,
	    httpNodeRoot: "/"+domain,
	    userDir:home,
	    flowFile:home+"/flows_"+domain+".json",
	    servicePortSofia2 : servicePort,
	    functionGlobalContext: { }    
	    // enables global context
	};



// Create a server
var server = http.createServer(app);



// Initialise the runtime with a server and settings
RED.init(server,settings);

// Serve the editor UI from /red
app.use(settings.httpAdminRoot,RED.httpAdmin);

// Serve the http nodes UI from /api
app.use(settings.httpNodeRoot,RED.httpNode);



server.listen(port);

server.on('error', (e) => {
  if (e.code == 'EADDRINUSE') {
    console.log("Node-Red Manager. child.js. WARNING! The port: "+ port +" is in use. Cannot create NodeRed Instance");
  }else{
	  console.log("Node-Red Manager. child.js. Error generico en server del motor de flujos");
  }
});




// Start the runtime
RED.start().then(function(){
		var env = {
			domainStarted: domain,
			startedAtPort: port
		};
		process.send(env);
    });


//Comunicacion con el padre.
function comunicationProcess(input) {
  
   var message ="";
   if(input!=null && input.msg!=undefined){

   	  message = input.msg;
   
   }else{
   	  message = input;
   
   }

	switch(message) {
	    case 'stop':
	        RED.stop();
			server.close();
			setTimeout(function() {
				process.exit(1);
			}, 2000);
			break;
	    case 'kill':
	        RED.stop();
	        server.close();
	        setTimeout(function() {
				process.exit(1);
			}, 2000);
	    default:
	       
	}
  
}

process.on('message', function(m) {
	comunicationProcess(m);
});
