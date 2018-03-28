(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('datasourceSolverService', DatasourceSolverService);

  /** @ngInject */
  function DatasourceSolverService(socketService, sofia2HttpService, $interval, $rootScope) {
      var vm = this;
      vm.gadgetToDatasource = {};
      
      vm.pendingDatasources = {};
      vm.poolingDatasources = {};
      vm.streamingDatasources = {};

      sofia2HttpService.setDashboardEngineCredentialsAndLogin().then(function(a){
        console.log("Login Rest OK, connecting SockJs Stomp dashboard engine");
        socketService.connect();
      }).catch(function(e){
        console.log("Login Rest Fail: " + JSON.stringify(e));
      })

      //datasource {"name":"name","type":"query","refresh":"refresh",triggers:[{params:{where:[],project:[],filter:[]},emiter:""}]}
      vm.registerDatasources = function(datasources){
        for(var key in datasources){
          var datasource = datasources[key];
          if(datasource.type=="query"){//Query datasource. We don't need RT conection only request-response
            if(datasource.refresh==0){//One shot datasource, we don't need to save it, only execute it once
              //vm.pendingDatasources[datasource.name] = datasource;
              for(var i = 0; i< datasource.triggers.length;i++){
                socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emitTo, callback: vm.emitToTargets}]);
              }
            }
            else{//Interval query datasource, we need to register this datasource in order to pooling results
              vm.poolingDatasources[datasource.name] = datasource;
              var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
                function(datasource){
                  for(var i = 0; i< datasource.triggers.length;i++){
                    socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emitTo, callback: vm.emitToTargets}]);
                  }
                },datasource.refresh * 1000, 0, true, datasource
              );
              vm.poolingDatasources[datasource.name].intervalId = intervalId;
            }
          }
          else{//Streaming datasource
            //TODO
          }
        }
      };

      function connectRegisterSingleDatasourceAndFirstShot(datasource){
        if(datasource.type=="query"){//Query datasource. We don't need RT conection only request-response
          if(datasource.refresh==0){//One shot datasource, we don't need to save it, only execute it once
            //vm.pendingDatasources[datasource.name] = datasource;
            for(var i = 0; i< datasource.triggers.length;i++){
              socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emitTo, callback: vm.emitToTargets}]);
            }
          }
          else{//Interval query datasource, we need to register this datasource in order to pooling results
            vm.poolingDatasources[datasource.name] = datasource;
            var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
              function(datasource){
                for(var i = 0; i< datasource.triggers.length;i++){
                  socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emitTo, callback: vm.emitToTargets}]);
                }
              },datasource.refresh * 1000, 0, true, datasource
            );
            vm.poolingDatasources[datasource.name].intervalId = intervalId;
          }
        }
        else{//Streaming datasource
          //TODO
        }
      }

      //Method from gadget to drill up and down the datasource
      vm.drillDown = function(gadgetId){}
      vm.drillUp = function(gadgetId){}

      vm.updateDatasourceTriggerAndShot = function(gadgetID, updateInfo){
        var accessInfo = vm.gadgetToDatasource[gadgetID];
        var dsSolver = vm.poolingDatasources[accessInfo.ds].triggers[accessInfo.index];
        updateQueryParams(dsSolver,updateInfo);
        var solverCopy = angular.copy(dsSolver);
        solverCopy.params.filter = solverCopy.params.filter.map(function(elem){
          return elem.data[0];
        })
        socketService.sendAndSubscribe({"msg":fromTriggerToMessage(solverCopy,accessInfo.ds),id: angular.copy(gadgetID), type:"filter", callback: vm.emitToTargets});
      }

      //update info has the filter, group, project id to allow override filters from same gadget and combining with others
      function updateQueryParams(trigger, updateInfo){
        var index = 0;//index filter
        var overwriteFilter = trigger.params.filter.filter(function(sfilter,i){
          if(sfilter.id == updateInfo.filter.id){
            index = i;
          }
          return sfilter.id == updateInfo.filter.id;
        });
        if (overwriteFilter.length>0){//filter founded, we need to override it
          if(updateInfo.filter.data.length==0){//with empty array we delete it, remove filter action
            trigger.params.filter.splice(index,1); 
          }
          else{ //override filter, for example change filter data and no adding
            overwriteFilter[0].data = updateInfo.filter.data;  
          }
        }
        else{
          trigger.params.filter.push(updateInfo.filter);
        }

        if(updateInfo.group){//For group that only change in drill options, we need to override all elements
          trigger.params.group = updateInfo.group;
        }

        if(updateInfo.project){//For project that only change in drill options, we need to override all elements
          trigger.params.project = updateInfo.project;
        }
      }

      vm.registerSingleDatasourceAndFirstShot = function(datasource){
        if(datasource.type=="query"){//Query datasource. We don't need RT conection only request-response
          if(!(datasource.name in vm.poolingDatasources)){
            vm.poolingDatasources[datasource.name] = datasource;
            vm.poolingDatasources[datasource.name].triggers[0].listeners = 1;
            vm.gadgetToDatasource[datasource.triggers[0].emitTo] = {"ds":datasource.name, "index":0};
          }
          else if(!(datasource.triggers[0].emitTo in vm.gadgetToDatasource)){
            vm.poolingDatasources[datasource.name].triggers.push(datasource.triggers[0]);
            var newposition = vm.poolingDatasources[datasource.name].triggers.length-1
            vm.poolingDatasources[datasource.name].triggers[newposition].listeners = 1;
            vm.gadgetToDatasource[datasource.triggers[0].emitTo] = {"ds":datasource.name, "index":newposition};
          }
          else{
            var gpos = vm.gadgetToDatasource[datasource.triggers[0].emitTo];
            vm.poolingDatasources[datasource.name].triggers[gpos.index].listeners++;
          }
          //One shot datasource, for pooling and 
          //vm.pendingDatasources[datasource.name] = datasource;
          for(var i = 0; i< datasource.triggers.length;i++){
            socketService.sendAndSubscribe({"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: angular.copy(datasource.triggers[i].emitTo), type:"refresh", callback: vm.emitToTargets});
          }
          if(datasource.refresh!=0){//Interval query datasource, we need to register this datasource in order to pooling results
            var i;
            var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
              function(datasource){
                for(var i = 0; i< datasource.triggers.length;i++){
                  socketService.sendAndSubscribe({"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: angular.copy(datasource.triggers[i].emitTo), type:"refresh", callback: vm.emitToTargets});
                }
              },datasource.refresh * 1000, 0, true, datasource
            );
            vm.poolingDatasources[datasource.name].intervalId = intervalId;
          }
        }
        else{//Streaming datasource
          //TODO
        }
      }

      function fromTriggerToMessage(trigger,dsname){
        var baseMsg = trigger.params;
        baseMsg.ds = dsname;
        return baseMsg;
      }

      vm.emitToTargets = function(id,name,data){
        //pendingDatasources
        $rootScope.$broadcast(id,
          {
            type: "data",
            name: name,
            data: JSON.parse(data.data)
          }
        );
      }

      vm.registerDatasource = function(datasource){
        vm.poolingDatasources[datasource.name] = datasource;
      }

      vm.registerDatasourceTrigger = function(datasource, trigger){//add streaming too
        if(!(datasource.name in vm.poolingDatasources)){
          vm.poolingDatasources[datasource.name] = datasource;
        }
        vm.poolingDatasources[name].triggers.push(trigger);
        //trigger one shot
      }

      vm.unregisterDatasourceTrigger = function(name,emiter){

        if(name in vm.pendingDatasources && vm.pendingDatasources[name].triggers.length == 0){
          vm.pendingDatasources[name].triggers = vm.pendingDatasources[name].triggers.filter(function(trigger){return trigger.emitTo!=emiter});

          if(vm.pendingDatasources[name].triggers.length==0){
            delete vm.pendingDatasources[name];
          }
        }
        if(name in vm.poolingDatasources && vm.poolingDatasources[name].triggers.length == 0){
          var trigger = vm.poolingDatasources[name].triggers.filter(function(trigger){return trigger.emitTo==emiter});
          trigger.listeners--;
          if(trigger.listeners==0){
            vm.poolingDatasources[name].triggers = vm.poolingDatasources[name].triggers.filter(function(trigger){return trigger.emitTo!=emiter});
          }

          if(vm.poolingDatasources[name].triggers.length==0){
            $interval.clear(vm.poolingDatasources[datasource.name].intervalId);
            delete vm.poolingDatasources[name];
          }
        }
        if(name in vm.streamingDatasources && vm.streamingDatasources[name].triggers.length == 0){
          vm.streamingDatasources[name].triggers = vm.streamingDatasources[name].triggers.filter(function(trigger){return trigger.emitTo!=emiter});

          if(vm.streamingDatasources[name].triggers.length==0){
            /*Unsubsuscribe TODO*/
            delete vm.streamingDatasources[name];
          }
        }
      }

      vm.disconnect = function(){
        socketService.disconnect();
      }
  }
})();
