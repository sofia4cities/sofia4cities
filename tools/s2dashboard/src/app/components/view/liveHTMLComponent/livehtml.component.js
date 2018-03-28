(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('livehtml', {
      templateUrl: 'app/components/view/liveHTMLComponent/livehtml.html',
      controller: LiveHTMLController,
      controllerAs: 'vm',
      bindings:{
        id:"=?",
        livecontent:"<",
        datasource:"<",
        datastatus:"=?"
      }
    });

  /** @ngInject */
  function LiveHTMLController($log, $scope, $element, $mdCompiler, $compile, datasourceSolverService,sofia2HttpService,interactionService,utilsService) {
    var vm = this;
    $scope.ds = [];
    vm.status = "initial";

    vm.$onInit = function(){
      //register Gadget in interaction service when gadget has id
      if(vm.id){
        interactionService.registerGadget(vm.id);
      }
      //Activate incoming events
      vm.unsubscribeHandler = $scope.$on(vm.id,eventLProcessor);
      compileContent();
    }

    vm.$onChanges = function(changes,c,d,e) {
      if("datasource" in changes && changes["datasource"].currentValue){
        refreshSubscriptionDatasource(changes.datasource.currentValue, changes.datasource.previousValue)
      }
      else{
        compileContent();
      }
    };

    $scope.getTime = function(){
      var date  = new Date();
      return date.getTime();
    }

    vm.insertSofia2Http = function(token, clientPlatform, clientPlatformId, ontology, data){
      sofia2HttpService.insertSofia2Http(token, clientPlatform, clientPlatformId, ontology, data).then(
        function(e){
          console.log("OK Rest: " + JSON.stringify(e));
        }).catch(function(e){
          console.log("Fail Rest: " + JSON.stringify(e));
        });
    }

    vm.$onDestroy = function(){
      if($scope.unsubscribeHandler){
        $scope.unsubscribeHandler();
        $scope.unsubscribeHandler=null;
        datasourceSolverService.unregisterDatasourceTrigger(oldDatasource.name,oldDatasource.name);
      }
    }

    function compileContent(){
      var parentElement = $element[0];
      $mdCompiler.compile({
        template: vm.livecontent
      }).then(function (compileData) {
        compileData.link($scope);
        $element.empty();
        $element.prepend(compileData.element)
      });
    }

    function refreshSubscriptionDatasource(newDatasource, oldDatasource) {
      if($scope.unsubscribeHandler){
        $scope.unsubscribeHandler();
        $scope.unsubscribeHandler=null;
        datasourceSolverService.unregisterDatasourceTrigger(oldDatasource.name,oldDatasource.name);
      }

      datasourceSolverService.registerSingleDatasourceAndFirstShot(//Raw datasource no group, filter or projections
        {
          type: newDatasource.type,
          name: newDatasource.name,
          refresh: newDatasource.refresh,
          triggers: [{params:{filter:[],group:[],project:[]},emitTo:vm.id}]
        }
      );
    };

    function eventLProcessor(event,dataEvent){
      if(dataEvent.type === "data" && dataEvent.data.length===0){
        vm.type="nodata";
      }
      else{
        switch(dataEvent.type){
          case "data":
            switch(dataEvent.name){
              case "refresh":
                if(vm.status === "initial" || vm.status === "ready"){
                  $scope.ds = dataEvent.data;
                }
                else{
                  console.log("Ignoring refresh event, status " + vm.status);
                }
                break;
              case "add":
                $scope.ds.concat(data);
                break;
              case "filter":
                if(vm.status === "pending"){
                  $scope.ds = dataEvent.data;
                  vm.status = "ready";
                }
                break;
              default:
                console.error("Not allowed data event: " + dataEvent.name);
                break;
            } 
            break;
          case "filter":
            vm.status = "pending";
            vm.type = "loading";
            if(!vm.datastatus){
              vm.datastatus = {};
            }
            for(var index in dataEvent.data){
              vm.datastatus[angular.copy(dataEvent.data[index].field)] = angular.copy(dataEvent.data[index].value);
            }
            datasourceSolverService.updateDatasourceTriggerAndShot(vm.id,buildFilterStt(dataEvent));
            break;
          default:
            console.error("Not allowed event: " + dataEvent.type);
            break;
        }
      }
      utilsService.forceRender($scope);
    }

    function buildFilterStt(dataEvent){
      return {
        filter: {
          id: dataEvent.id,
          data: dataEvent.data.map(
            function(f){
              //quotes for string identification
              if(typeof f.value === "string"){
                f.value = "\"" + f.value + "\""
              }
              return {
                field: f.field,
                op: "=",
                exp: f.value
              }
            }
          )
        } , 
        group:[], 
        project:vm.projects
      }
    }
  }
})();
