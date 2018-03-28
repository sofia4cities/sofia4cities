(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('livehtml', {
      templateUrl: 'app/components/view/liveHTMLComponent/livehtml.html',
      controller: LiveHTMLController,
      controllerAs: 'vm',
      bindings:{
        livecontent:"<",
        datasource:"<"
      }
    });

  /** @ngInject */
  function LiveHTMLController($log, $scope, $element, $mdCompiler, $compile, datasourceSolverService,sofia2HttpService) {
    var vm = this;
    $scope.ds = [];

    vm.$onInit = function(){
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
      $scope.unsubscribeHandler = $scope.$on(newDatasource.name,function(event,data){
        $scope.ds = data;
      });

      datasourceSolverService.registerSingleDatasourceAndFirstShot(//Raw datasource no group, filter or projections
        {
          type: newDatasource.type,
          name: newDatasource.name,
          refresh: newDatasource.refresh,
          triggers: [{params:{filter:[],group:[],project:[]},emitTo:newDatasource.name}]
        }
      );
    };
  }
})();
