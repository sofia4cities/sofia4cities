(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('gadgetManagerService', GadgetManagerService);

  /** @ngInject */
  function GadgetManagerService() {
      var vm = this;
      vm.dashboardModel = {};
      vm.selectedpage = 0

      vm.setDashboardModelAndPage = function(dashboard,selectedpage){
        vm.dashboardModel = dashboard;
        vm.selectedpage = selectedpage;
      }

      vm.findGadgetById = function(gadgetId){
        var page = vm.dashboardModel.pages[vm.selectedpage];
        for(var layerIndex in page.layers){
          var layer = page.layers[layerIndex];
          var gadgets = layer.gridboard.filter(function(gadget){return gadget.id === gadgetId});
          if(gadgets.length){
            return gadgets[0];
          }
        }
        return null;
      }
      
  }
})();
