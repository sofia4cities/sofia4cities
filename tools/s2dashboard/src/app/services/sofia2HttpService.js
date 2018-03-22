(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('sofia2HttpService', Sofia2HttpService);

  /** @ngInject */
  function Sofia2HttpService($http, $log, __env, $rootScope) {
      var vm = this;

      vm.getDatasources = function(){
        return $http.get(__env.endpointSofia2ControlPanel + '/datasources/getUserGadgetDatasources');
      }

      vm.getDatasourceById = function(datasourceId){
        return $http.get(__env.endpointSofia2ControlPanel + '/datasources/getDatasourceById/' + datasourceId);
      }

      vm.getGadgetConfigById = function(gadgetId){
        return $http.get(__env.endpointSofia2ControlPanel + '/gadgets/getGadgetConfigById/' + gadgetId);
      }

      vm.getUserGadgetsByType = function(type){
        return $http.get(__env.endpointSofia2ControlPanel + '/gadgets/getUserGadgetsByType/' + type);
      }

      vm.getGadgetMeasuresByGadgetId = function(gadgetId){
        return $http.get(__env.endpointSofia2ControlPanel + '/gadgets/getGadgetMeasuresByGadgetId/' + gadgetId);
      }

      vm.saveDashboard = function(id, dashboard){
        return $http.put(__env.endpointSofia2ControlPanel + '/dashboards/savemodel/' + id, {"model":dashboard.data.model,"visible":dashboard.public});
      }

      vm.setDashboardEngineCredentialsAndLogin = function () {
        var authdata = btoa(__env.dashboardEngineUsername + ':' + __env.dashboardEnginePassword);

        $rootScope.globals = {
            currentUser: {
                username: __env.dashboardEngineUsername,
                authdata: __env.dashboardEnginePassword
            }
        };

        $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
        return $http.get(__env.endpointSofia2DashboardEngine + __env.dashboardEngineLoginRest);
      };

      vm.getDashboardModel = function(id){
        return $http.get(__env.endpointSofia2ControlPanel + '/dashboards/model/' + id);
      }

      vm.insertSofia2Http = function(token, clientPlatform, clientPlatformId, ontology, data){
        return $http.get(__env.sofia2RestUrl + "/client/join?token=" + token + "&clientPlatform=" + clientPlatform + "&clientPlatformId=" + clientPlatformId).then(
          function(e){
            $http.defaults.headers.common['Authorization'] = e.data.sessionKey;
            return $http.post(__env.sofia2RestUrl + "/ontology/" + ontology,data);
          }
        )
      }
  };
})();
