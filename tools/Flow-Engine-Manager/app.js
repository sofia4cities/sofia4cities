//Propiedades para configurar
var administrationPort = 10000;
var proxyPort = 5050;
var domains_START = '/home/rtvachet/bin/nodeRed/Node-RED/Flow-Engine-Manager/domains_START.txt';

//var domains_START = './domains_START.txt';
//Fin de Propiedades para configurar



var http = require('http');
var express = require("express");
var path = require("path");
var fs = require("fs");
var bodyParser = require('body-parser');
var kill = require('tree-kill');
var util = require("util");

var pusage = require('pidusage');
var promise = require('promise');
var waitUntil = require('wait-until');




// Create an Express app
var app = express();


// Add a simple route for static content served from 'public'
app.use("/", express.static("public"));

//JSON
app.use(bodyParser.urlencoded({
    extended: false
}));
app.use(bodyParser.json());



//Variables globales
var settings = {};
var serverNodeRed;
var listChildsRed = [];
var nameChild = "";
var listSyncDomain = [];
var objectSync = null;


var listDataFlow = [];


//Global proxy
var usersPorts = {};

/**
 * Comprueba que el puerto donde se va a dar de alta el dominio no esta siendo usado por otro usuario.
 * @author clobato
 */
function portInUse(port) {

    var newPort = port;

    var fd = fs.openSync(domains_START, 'r');

    var readFile = fs.readFileSync(domains_START).toString().split("\n");
    var isUsed = false;

    for (i in readFile) {

        if (readFile[i] != undefined) {
            splitRead = readFile[i].split("#");

            if (splitRead != undefined && splitRead != '') {

                portFile = splitRead[1].split(":");
                stateFile = splitRead[4].split(":");

                if (portFile[1] == port) {

                    console.log("Node-Red Manager. app.js-->portInUse(): WARNING! The "+port+"  Address in use, try again!");

                    isUsed = true;

                }

            }

        }

    }

    fs.closeSync(fd);
    return isUsed;
}

/**
 * Append nuevos dominios al un fichero.
 * @author clobato
 */
function appendDomain(file, line) {

    try {
        fs.appendFileSync(file, line + "\n");
		
        console.log("Node-Red Manager. app.js-->appendDomain(). Writting in " + file + ": " + line.toString());
    } catch (err) {
        throw err;
    }
}

/*
 * Obtiene el dominio en un fichero
 * @author clobato
 */
function getDomain(readFile, queryDomain) {
    var domain = [];
    var index = 0;
    for (i in readFile) {

        splitRead = readFile[i].split("#");
        splitD = splitRead[0].split(":");

        if (splitD[1] == queryDomain) {
            domain[index] = readFile[i];
            index++;
        }


    }


    return domain;

}


/*
 * Recover  domains on START
 * @author clobato
 */
function recoverDomain() {

    //INFO START APP
    var info = require('./info-startApp');
    
    var readFile = fs.readFileSync(domains_START).toString().split("\n");

    for (i in readFile) {
		try{
			splitRead = readFile[i].split("#");

			if (splitRead != null && splitRead != undefined) {
				var domain = splitRead[0];

				var port = splitRead[1];
				var home = splitRead[2];
				var servicePort = splitRead[3];
				var state = splitRead[4];

				 
				 if(state!=undefined){
					var stateSTART = state.split(":")[1].substr(0,state.split(":")[1].length-1);
		 
				 }
				
				if (domain != undefined && state != undefined && stateSTART=="START") {
				   
				   //Child Process [NODE-RED]      
					var fork = require('child_process').fork;
					var env = {
						domain: domain.split(":")[1],
						port: port.split(":")[1],
						home: home.split(":")[1],
						servicePort: servicePort.split(":")[1]
					};

					nameChild = domain.split(":")[1];
					
	
					listChildsRed[nameChild] = fork(__dirname + '/child.js', {
						env: env
					});
					
					
					listChildsRed[nameChild].on('uncaughtException', function (err) {
						console.log("Node-Red Manager. app.js-->recoverDomain(). Error in domain: "+nameChild);
					});
					
					
					//Asignamos el puerto para el dominio del  proxy. 
					//Lo metemeos aqui, ya que tenemos garantias de que el puerto ya estÃ¡ levantado y asÃ­ el proxy no le redirecciona
					//peticiones antes
					listChildsRed[nameChild].on('message', function(data) {
						console.log("Node-Red Manager. app.js-->recoverDomain(). NodeRED instance started at port: "+data.startedAtPort)
						usersPorts[data.domainStarted] = data.startedAtPort;
					});
				}

			}
		} catch (err) {
			console.log("Node-Red Manager. app.js-->recoverDomain(). Error starting domain");
		}
		
    }
   
}

/*
 * Obtiene el valor de la cpu y la memoria del proceso 
 * return Promise
 * @author clobato
 */

function stats(pid, listAllDomain, i) {
    return new Promise((resolve, reject) => {
        
		if(pid==null){
              resolve(listAllDomain[i]);
        }
		pusage.stat(pid, (err, data) => {
            if (err) {
                return reject(err)
            }

            listAllDomain[i].memory = data.memory;
            listAllDomain[i].cpu = data.cpu;
            resolve(listAllDomain[i])


        })
    })
}


/*
 * Obtiene la lista final del dominio con todos sus datos. 
 * return Promise
 * @author clobato
 */
function showListAllDomain(listAllDomain) {
    
	console.log("Node-Red Manager. app.js-->showListAllDomain()");
	
	listDataFlow = [];

    return new Promise(function(resolve, reject) {
		var indexDataFlow=0;
		
        for (var i = 0; i < listAllDomain.length; i++) {
            if (listAllDomain[i].state == 'START') {
                var pid = listChildsRed[listAllDomain[i].domain].pid;

            } else {
                var pid = null;

            }

           stats(pid, listAllDomain, i).then((data) => {
                listDataFlow[indexDataFlow]=data;
                indexDataFlow++;
           
            })
            
        }
		
		
		waitUntil(500, 8, function condition() {
			return (indexDataFlow>=listAllDomain.length ? true : false);
		}, function done(result) {
			resolve(listDataFlow); 
		});
	})


}


/*
 * Obtiene todos los dominios 
 * @author clobato
 */
function getAllDomain() {

    var listAllDomain = []; 
    var cpu = "";
    var memory = "";
    
    var readFile = fs.readFileSync(domains_START).toString().split("\n");

	var listDomainsIndex=0;
    for (i in readFile) {
        splitRead = readFile[i].split("#");

        if (splitRead != null && splitRead != undefined) {
            var domain = splitRead[0];
            var port = splitRead[1];
            var home = splitRead[2];
            var servicePort = splitRead[3];
            var state = splitRead[4];
			if (domain != undefined && state != undefined) {
                //console.log(domain + "  --  " + port + "  --  " + state);
				
				var runtimeState;
				if(state.split(":")[1].substr(0,state.split(":")[1].length-1)=='STOP'){
					runtimeState='STOP';
				}else{
					var runtimeState = (usersPorts[domain.split(":")[1]]==undefined) ? 'STARTING': 'START';
				}
				
				
                var objectJSON = {
                    domain: domain.split(":")[1],
                    port: port.split(":")[1],
                    home: home.split(":")[1],
                    servicePort : servicePort.split(":")[1],
                    state: state.split(":")[1].substr(0,state.split(":")[1].length-1),
					runtimeState: runtimeState,
                    cpu: cpu, 
                    memory: memory
                }
                 listAllDomain[listDomainsIndex++] = objectJSON; 
            }

        }


    }


   return listAllDomain;
}

/*
 * Obtiene todos los status
 * @author clobato
 */
function getStatusDomain(req){

   var queryDomains = JSON.parse(req.query.domains); 
    var listAllDomain = []; 
    var memory = "";
     var cpu = "";

    
    var readFile = fs.readFileSync(domains_START).toString().split("\n");

    console.log("Node-Red Manager. app.js-->getStatusDomain(). List Domains and states");
    var addedDomains=0;
    for (i in readFile) {
        splitRead = readFile[i].split("#");

        if (splitRead != null && splitRead != undefined) {
            var domain = splitRead[0];
            var port = splitRead[1];
            var home = splitRead[2];
            var servicePort = splitRead[3];
            var state = splitRead[4];
            if (domain != undefined && state != undefined) {
               
                if(queryDomains.indexOf(domain.split(":")[1])!=-1){

                    var runtimeState;
                    if(state.split(":")[1].substr(0,state.split(":")[1].length-1)=='STOP'){
                        runtimeState='STOP';
                    }else{
                        var runtimeState = (usersPorts[domain.split(":")[1]]==undefined) ? 'STARTING': 'START';
                    }
                    
                    var objectJSON = {
                        domain: domain.split(":")[1],
                        port: port.split(":")[1],
                        home: home.split(":")[1],
                        servicePort : servicePort.split(":")[1],
                        state: state.split(":")[1].substr(0,state.split(":")[1].length-1),
                        runtimeState: runtimeState,
                        cpu: cpu, 
                        memory: memory 
                    }
                     listAllDomain[addedDomains++] = objectJSON; 
                }
            }

        }


    }
	console.log("Node-Red Manager. app.js-->getStatusDomain(). Number of domains returned: "+listAllDomain.length)
  return listAllDomain;
}




/*
 * Obtiene una linea del fichero con el dominio completo.
 * @author clobato
 */
function getLineFileDomain(queryDomain) {
    var line = "";
    var readFile = fs.readFileSync(domains_START).toString().split("\n");
    for (i in readFile) {
        var domainFile = readFile[i].split('#')[0].split(':')[1];
        if (domainFile != undefined && domainFile == queryDomain) {

            line = readFile[i];
        }

    }

    return line;
}




/*
 * Actualiza todo el fichero
 * @author clobato
 */
function updateFileDomainsStart(file, queryDomain, line) {

    //console.log("Updating file DomainsStart")

    var readFile = fs.readFileSync(file).toString().split("\n");

    var fd = fs.openSync(file, 'w');
    fs.writeSync(fd, line + "\n");
    fs.closeSync(fd);

    for (i in readFile) {
        var domainFile = readFile[i].split('#')[0].split(':')[1];
        if (domainFile != undefined && domainFile != queryDomain) {

            fs.appendFileSync(file, readFile[i] + "\n");
        }

    }


}


/*
 * Elimina aquellos dominios del fichero que no estan en la lista
 *
 * @author clobato.
 */
/*function deleteFileSyncDomain(listSyncDomain) {

    var readFile = fs.readFileSync(domains_START).toString().split("\n");
    var line = " ";
    var enc = false;
    var index = 0;

	var indexQueryDomains=0;
	var queryDomains = [];
	for (index in listSyncDomain) {
		var objSync = listSyncDomain[index];
		queryDomains[indexQueryDomains++]=objSync.domain;
	}
	
	console.log("##########################3");
	console.log(queryDomains);
	console.log("##########################3");
	 
    while (index < readFile.length && !enc) {
        var domainFile = readFile[index].split('#')[0].split(':')[1];
		console.log("##########################3");
		console.log(domainFile);
		console.log(queryDomains.indexOf(domainFile)!=-1)
		console.log("##########################3");
        if (queryDomains.indexOf(domainFile)!=-1) {
            enc = true;
            fs.appendFileSync(domains_START, readFile[index] + "\n");

        }

        index++;
    }


}*/


/*
 * Sincroniza con el fichero de dominios la lista 
 *
 * @author clobato
 */

function SynchronizingDomains(file, listSyncDomain) {

	console.log("Node-Red Manager. app.js-->SynchronizingDomains()");

    var fd = fs.openSync(domains_START, 'w');
	//var line="";
    fs.writeSync(fd, "");
    fs.closeSync(fd);
    //deleteFileSyncDomain(listSyncDomain);

    for (index in listSyncDomain) {

        var objSync = listSyncDomain[index];


        if (objSync != undefined) {

            var resultGetDomain = getLineFileDomain(objSync.domain);

            if (resultGetDomain != null && resultGetDomain != undefined && resultGetDomain != "") {

                var stateGetDomain = resultGetDomain.toString().split('#');
                var state = stateGetDomain[4].split(':')[1].substr(0, stateGetDomain[4].split(':')[1].length - 1);


                if (objSync.state != state) {
                    switch (objSync.state) {

                        case 'STOP':
                            var line = resultGetDomain.toString().replace("START", "STOP");
                            //Actualizamos el fichero de estados con la nueva situaciÃ³n.
                            updateFileDomainsStart(domains_START, objSync.domain, line);
                            break;
                        case 'START':
                            var line = resultGetDomain.toString().replace("STOP", "START");
                            //Actualizamos el fichero de estados con la nueva situaciÃ³n.
                            updateFileDomainsStart(domains_START, objSync.domain, line);
                            
                            break;

                        default:

                    }


                }
            } else { //No encuentra en la lista de dominios; Hay que insertarlo

                var line = "[Domain:" + objSync.domain + "#Port:" + objSync.port + "#Home:" + objSync.home + "#servicePort:" + objSync.servicePort + "#Estado:" + objSync.state + "]";
                updateFileDomainsStart(domains_START, objSync.domain, line);

            }
        }

    }


}




/*
 * POST createDomainMF
 * @author clobato
 */

app.post('/createDomainMF', function(req, res) {
    console.log("Node-Red Manager. app.js-->REST Method: createDomainMF()");
    var domain = req.body.domain;
    var port = req.body.port;
    var home = req.body.home;
    var servicePort = req.body.servicePort;

    //Comprobar si el puerto esta usado ya


    if (!portInUse(port)) {
        var estado = "STOP";
        var line = "[Domain:" + domain + "#Port:" + port + "#Home:" + home + "#servicePort:" + servicePort + "#Estado:" + estado + "]";

       try{
            //Append linea en el fichero de Domains
            appendDomain(domains_START, line);
            //End createDomainMF
			console.log("Node-Red Manager. app.js-->REST Method: createDomainMF(). Respuesta OK")
            res.send('OK');

       }catch(err){
         console.log("Node-Red Manager. app.js-->REST Method: createDomainMF(). Error:",err);
         res.statusCode = 404;
         res.send('Port unavailable');
       }
        

    }else{
		 console.log("Node-Red Manager. app.js-->REST Method: createDomainMF(). Respuesta 404")
		 res.statusCode = 404;
		 res.send('Port unavailable');
	}


});

/*
 * Post startDomainMF
 * @author clobato
 */

app.post('/startDomainMF', function(req, res) {

    console.log("Node-Red Manager. app.js-->REST Method: startDomainMF()");
	
	var domain = req.body.domain;
	
	//Primero consulta si el dominio estÃ¡ arrancado. En ese caso lo para (Reinicio)
	var childProcess = listChildsRed[domain];

    if (childProcess != null && childProcess != undefined) {
		console.log("Node-Red Manager. app.js-->REST Method: startDomainMF(). Domain is started. Restarts domain")

        //Enviamos seÃ±al de stop al proceso
        console.log("STOP ChildProcess_PID: " + childProcess.pid);
        childProcess.send({
            msg: 'stop'
        });

        //Obtenemos la linea a actualizar
        var line = getLineFileDomain(domain);
        line = line.replace("START", "STOP");
        //Actualizamos el fichero de estados con la nueva situaciÃ³n.
        updateFileDomainsStart(domains_START, domain, line);
    }
	
	

    try{
		var port = req.body.port;
		var home = req.body.home;
		var servicePort = req.body.servicePort;

		var estado = "START"

		var line = "[Domain:" + domain + "#Port:" + port + "#Home:" + home + "#servicePort:" + servicePort + "#Estado:" + estado + "]";


		//Actualizamos el fichero de estados con la nueva situaciÃ³n.
		updateFileDomainsStart(domains_START, domain, line);


		//Child Process [NODE-RED]      
		var fork = require('child_process').fork;
		var env = {
			domain: domain,
			port: port,
			home: home,
			servicePort: servicePort
		};

		nameChild = domain;

		listChildsRed[nameChild] = fork(__dirname + '/child.js', {
			env: env
		});
		
		
		listChildsRed[nameChild].on('uncaughtException', function (err) {
			console.log("Node-Red Manager. app.js-->REST Method: startDomainMF(). Error ejecutantdo Script de proceso hijo")
		});
		
		
		//Asignamos el puerto para el dominio del  proxy. 
		//Lo metemeos aqui, ya que tenemos garantias de que el puerto ya estÃ¡ levantado y asÃ­ el proxy no le redirecciona
		//peticiones antes
		listChildsRed[nameChild].on('message', function(data) {
			console.log("Node-Red Manager. app.js-->REST Method: startDomainMF(). NodeRED instance started ad port: "+data.startedAtPort);			
			usersPorts[data.domainStarted] = data.startedAtPort;
		});


		console.log("Node-Red Manager. app.js-->REST Method: startDomainMF(). Successs ChildProcess_PID:" + listChildsRed[nameChild].pid);
		res.send('OK');
	} catch (err) {
		console.log("Node-Red Manager. app.js-->REST Method: startDomainMF(). Error 500")
		res.statusCode = 500;
        res.send('Error');
    }



});
 

/*
 * PUT stopDomainMF [node-red]
 * @author clobato
 */

app.put('/stopDomainMF/:domain', function(req, res) {
    console.log("Node-Red Manager. app.js-->REST Method: stopDomainMF()");

    var queryDomain = req.params.domain;


    console.log("Node-Red Manager. app.js-->REST Method: stopDomainMF(). Domain: " + queryDomain);

    var childProcess = listChildsRed[queryDomain];

    if (childProcess != null && childProcess != undefined) {


        try{
                     //Lo borra de la lista utilizada por el proxy
                delete usersPorts[queryDomain];
                //Enviamos seÃ±al de stop al proceso
                console.log("Node-Red Manager. app.js-->REST Method: stopDomainMF(). STOP ChildProcess_PID: " + childProcess.pid);
                childProcess.send({
                    msg: 'stop'
                });
                
                pusage.unmonitor(listChildsRed[queryDomain].pid);
                delete listChildsRed[queryDomain];
                

                //Obtenemos la linea a actualizar
                var line = getLineFileDomain(queryDomain);
                line = line.replace("START", "STOP");
                //Actualizamos el fichero de estados con la nueva situaciÃ³n.
                updateFileDomainsStart(domains_START, queryDomain, line);
        }catch(err){
               console.log("Node-Red Manager. app.js-->REST Method: stopDomainMF(). Error",err);
        }

		
    } else {
        res.statusCode = 200;//No es un error
        res.send('ChildProcess_PID not running now!');
    }
	 console.log("Node-Red Manager. app.js-->REST Method: stopDomainMF(). OK")
     res.send('OK');
});


/*
 * DELETE deleteDomain
 * @author clobato
 */

app.delete('/deleteDomainMF/:domain', function(req, res) {
    console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF()");
   
    var queryDomain = req.params.domain;

     try{
           var line = getLineFileDomain(queryDomain);

     }catch(err){
        console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). Error:",err);
        res.statusCode = 404;
        res.send('ChildProcess_PID not running now!');
     }

     //Obtenemos la linea a actualizar
    /*var line = getLineFileDomain(queryDomain);
     
    if(line==null || line == undefined){
        res.statusCode = 404;
        res.send('ChildProcess_PID not running now!');

    } */


    
    line = "";
    //Actualizamos el fichero de estados con la nueva situaciÃ³n.
    try{
        updateFileDomainsStart(domains_START, queryDomain, line);
    }catch(errFile){
        console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). Error",errFile);

    }

    var childProcess = listChildsRed[queryDomain];

    if (childProcess != null && childProcess != undefined) {
		
        try{
                //Lo borra de la lista utilizada por el proxy
            delete usersPorts[queryDomain];

            //Enviamos seÃ±al de stop al proceso
            console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). KILL ChildProcess_PID: " + childProcess.pid);
            childProcess.send({
                msg: 'kill'
            });
            
            setTimeout(function() {
                childProcess.kill('SIGINT');
            }, 1000);
            
            pusage.unmonitor(listChildsRed[queryDomain].pid);
            delete listChildsRed[queryDomain];

        }catch(errChildProcess){
            console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). Error",errChildProcess);
          }

       
    } else {
        console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). ChildProcess_PID not running now!")
        
    }
	console.log("Node-Red Manager. app.js-->REST Method: deleteDomainMF(). Ok");
    res.send('OK');
});




/*
 * GET getAllDomainMF
 * Uso de Promise
 * @author clobato
 */

app.get('/getAllDomainMF', function(req, res) {
	console.log("Node-Red Manager. app.js-->REST Method: getAllDomainMF()");
     try{
        showListAllDomain(getAllDomain()).then(function(data) {
			console.log("Node-Red Manager. app.js-->REST Method: getAllDomainMF(). Respuesta:"+JSON.stringify(data));
            res.send(JSON.stringify(data)); 
        })   
     }catch(err){
        console.log("Node-Red Manager. app.js-->REST Method: getAllDomainMF(). Error",err);
        res.send(err);
     }
     
});


/*
 * GET getDomainStatusMF
 * Uso de Promise
 * @author clobato
 */
app.get('/getDomainStatusMF', function(req, res){
	console.log("Node-Red Manager. app.js-->REST Method: getDomainStatusMF()");
 try{
  	showListAllDomain(getStatusDomain(req)).then(function(data) {
		var strData=JSON.stringify(data);
		console.log("Node-Red Manager. app.js-->REST Method: getDomainStatusMF(). Respuesta: "+strData);
        res.send(strData);
    	})
 }catch(err){
      console.log("Node-Red Manager. app.js-->REST Method: getDomainStatusMF(). Error",err);
      res.send(err);
   }
});


/*
 * GET getDomainMF
 * @author clobato
 */

app.get('/getDomainMF', function(req, res) {
    console.log("Node-Red Manager. app.js-->REST Method: getDomainMF()");

    var queryDomain = req.query.domain;
    
    var objectJSON = {};
    
    try{
       line =getLineFileDomain(queryDomain);

        if(line!=null && line!=undefined && line !=""){
          objectJSON = {
                domain: line.toString().split('#')[0].split(':')[1],
                port: line.toString().split('#')[1].split(':')[1],
                home: line.toString().split('#')[2].split(':')[1],
                servicePort: line.toString().split('#')[3].split(':')[1],
                state: line.toString().split('#')[4].split(':')[1].substr(0,line.toString().split('#')[4].split(':')[1].length-1),
            }

        }
        
		console.log("Node-Red Manager. app.js-->REST Method: getDomainMF(). Respuesta: "+JSON.stringify(objectJSON)); 
        res.send(JSON.stringify(objectJSON)); 
    }catch(err){
        console.log("Node-Red Manager. app.js-->REST Method: getDomainMF(). Error",err);
        res.send(err);

    }
    
  

});

/*
 * POST synchronizeMF
 * @author clobato
 */

app.post('/synchronizeMF', function(req, res) {
    console.log("Node-Red Manager. app.js-->REST Method: synchronizeMF()");
	
	var lst = req.body;

    try{
         for (i in lst.listDomain) {

        var objectSync = {
            domain: lst.listDomain[i].domain,
            port: lst.listDomain[i].port,
            home: lst.listDomain[i].home,
            servicePort: lst.listDomain[i].servicePort,
            state: lst.listDomain[i].state
        }
        listSyncDomain[i] = objectSync;
    }

    
    //Detiene los procesos que hay actualmente arrancados
    for(childProcess in listChildsRed){
        var processToKill = listChildsRed[childProcess];
        if (processToKill != null && processToKill != undefined) {

            //Enviamos seÃ±al de stop al proceso
            console.log("Node-Red Manager. app.js-->REST Method: synchronizeMF(). KILL ChildProcess_PID: " + processToKill.pid);
            processToKill.send({
                msg: 'kill'
            });
            
            setTimeout(function() {
                processToKill.kill('SIGINT');
            }, 1000);
            
            pusage.unmonitor(listChildsRed[queryDomain].pid);
            delete listChildsRed[queryDomain];
            
        }
    }
     
        //1.recorrer lista de sincronizados para ver si estan en la fichero de domains
        console.log("Node-Red Manager. app.js-->REST Method: synchronizeMF(). Synchronizing the list...");

        
        //Actualizar toda la lista de sincronizacion;
        SynchronizingDomains(domains_START, listSyncDomain);

        //Espera 1 segundo antes de empezar a levantar
        setTimeout(function() {
            recoverDomain();
			console.log("Node-Red Manager. app.js-->REST Method: synchronizeMF(). Respuesta Ok");
            res.send('OK');
        }, 1000);
    

    }catch(err){
        console.log("Node-Red Manager. app.js-->REST Method: synchronizeMF(). Error:",err);
        res.send(err);
    }

    

});


app.post('/stopMF', function(req, res) {
	//Detiene los procesos que hay actualmente arrancados
	console.log("Node-Red Manager. app.js-->REST Method: stopMF()");
	
    try{
        for(childProcess in listChildsRed){
        //console.log("Detiene un proceso");
        var processToKill = listChildsRed[childProcess];
        if (processToKill != null && processToKill != undefined) {

            //Enviamos seÃ±al de stop al proceso
            //console.log("KILL ChildProcess_PID: " + processToKill.pid);
            processToKill.send({
                msg: 'kill'
            });
            setTimeout(function() {
                processToKill.kill('SIGINT');
            }, 1000);
            
            pusage.unmonitor(listChildsRed[queryDomain].pid);
            delete listChildsRed[queryDomain];
            }
        }
        //console.log("Termina de detener procesos");
        
        //Se detiene a si mismo
        //console.log("Se detiene a si mismos");
        res.statusCode = 200;
		console.log("Node-Red Manager. app.js-->REST Method: stopMF()");
        res.send('OK');
        //console.log("Cierra el proxy");
        proxyNodeRed.close();
        //console.log("Cierra el server");
        server.close();
        //console.log("Cierra el proceso");
        process.exit();
    }catch(err){
        console.log("Node-Red Manager. app.js-->REST Method: stopMF()",err);
        res.send(err);
    }
    


});

// Create a server
var server = http.createServer(app);
server.listen(administrationPort);



server.on('error', (e) => {
    if (e.code == 'EADDRINUSE') {
        console.log("Node-Red Manager. app.js. WARNING! The administration port: "+administrationPort+" is in use");
        setTimeout(() => {
            server.listen(administrationPort);
        }, 1000);
    }
});


var proxyNodeRed = require('./proxy-nodered');

//Create Server Proxy --> Lo arranco asÃ­ porque se tiene que arrancar despues de los NodesRED, si no, el Websocket hace que pete el arranque
//del Node, porque como estÃ¡ en proceso de Arranque el proxy interpreta que se ha colgado
setTimeout(function() {
	console.log("Node-Red Manager. app.js. Start proxy to receive requests");
	proxyNodeRed.serverProxy(proxyPort, usersPorts); 
}, 1000);
