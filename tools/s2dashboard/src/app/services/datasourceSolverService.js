(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('datasourceSolverService', DatasourceSolverService);

  /** @ngInject */
  function DatasourceSolverService(socketService, sofia2HttpService, $interval, $rootScope) {
      var vm = this;
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
                socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets}]);
              }
            }
            else{//Interval query datasource, we need to register this datasource in order to pooling results
              vm.poolingDatasources[datasource.name] = datasource;
              var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
                function(datasource){
                  for(var i = 0; i< datasource.triggers.length;i++){
                    socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets}]);
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
              socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets}]);
            }
          }
          else{//Interval query datasource, we need to register this datasource in order to pooling results
            vm.poolingDatasources[datasource.name] = datasource;
            var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
              function(datasource){
                for(var i = 0; i< datasource.triggers.length;i++){
                  socketService.connectAndSendAndSubscribe([{"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets}]);
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

      vm.registerSingleDatasourceAndFirstShot = function(datasource){
        if(datasource.type=="query"){//Query datasource. We don't need RT conection only request-response
          if(datasource.refresh==0){//One shot datasource, we don't need to save it, only execute it once
            //vm.pendingDatasources[datasource.name] = datasource;
            for(var i = 0; i< datasource.triggers.length;i++){
              socketService.sendAndSubscribe({"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets});
            }
          }
          else{//Interval query datasource, we need to register this datasource in order to pooling results
            var i;
            if(!(datasource.name in vm.poolingDatasources)){
              vm.poolingDatasources[datasource.name] = datasource;
              vm.poolingDatasources[datasource.name].triggers[0].listeners = 1;
            }
            else if(i=vm.poolingDatasources[datasource.name].triggers.indexOf(datasource.triggers[0])!=-1){
              vm.poolingDatasources[datasource.name].triggers[i].listeners++;
            }
            var intervalId = $interval(/*Datasource passed as parameter in order to call every refresh time*/
              function(datasource){
                for(var i = 0; i< datasource.triggers.length;i++){
                  socketService.sendAndSubscribe({"msg":fromTriggerToMessage(datasource.triggers[i],datasource.name),id: datasource.triggers[i].emiter, callback: vm.emitToTargets});
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

      vm.emitToTargets = function(id,data){
        //pendingDatasources
        $rootScope.$broadcast(id,JSON.parse(data.data));
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
          vm.pendingDatasources[name].triggers = vm.pendingDatasources[name].triggers.filter(function(trigger){return trigger.emiter!=emiter});

          if(vm.pendingDatasources[name].triggers.length==0){
            delete vm.pendingDatasources[name];
          }
        }
        if(name in vm.poolingDatasources && vm.poolingDatasources[name].triggers.length == 0){
          var trigger = vm.poolingDatasources[name].triggers.filter(function(trigger){return trigger.emiter==emiter});
          trigger.listeners--;
          if(trigger.listeners==0){
            vm.poolingDatasources[name].triggers = vm.poolingDatasources[name].triggers.filter(function(trigger){return trigger.emiter!=emiter});
          }

          if(vm.poolingDatasources[name].triggers.length==0){
            $interval.clear(vm.poolingDatasources[datasource.name].intervalId);
            delete vm.poolingDatasources[name];
          }
        }
        if(name in vm.streamingDatasources && vm.streamingDatasources[name].triggers.length == 0){
          vm.streamingDatasources[name].triggers = vm.streamingDatasources[name].triggers.filter(function(trigger){return trigger.emiter!=emiter});

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
