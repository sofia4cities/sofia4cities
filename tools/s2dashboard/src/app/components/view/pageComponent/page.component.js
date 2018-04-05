(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('page', {
      templateUrl: 'app/components/view/pageComponent/page.html',
      controller: PageController,
      controllerAs: 'vm',
      bindings:{
        page:"=",
        editmode:"<",
        gridoptions:"<"
      }
    });

  /** @ngInject */
  function PageController($log, $scope, $mdSidenav, $mdDialog, datasourceSolverService) {
    var vm = this;
    vm.$onInit = function () {
      setTimeout(function () {
        vm.sidenav = $mdSidenav('left');
      }, 200);
    };

    vm.$postLink = function(){

    }

    vm.$onDestroy = function(){
      /*
      Not necesary
      datasourceSolverService.disconnect();
      */
    }

    function eventStop(item, itemComponent, event) {
      $log.info('eventStop', item, itemComponent, event);
    }

    function itemChange(item, itemComponent) {
      $log.info('itemChanged', item, itemComponent);
    }

    function itemResize(item, itemComponent) {
      debugger;
      $log.info('itemResized', item, itemComponent);
    }

    function itemInit(item, itemComponent) {
      $log.info('itemInitialized', item, itemComponent);
    }

    function itemRemoved(item, itemComponent) {
      $log.info('itemRemoved', item, itemComponent);
    }

    function gridInit(grid) {
      $log.info('gridInit', grid);
    }

    function gridDestroy(grid) {
      $log.info('gridDestroy', grid);
    }

    vm.prevent = function (event) {
      event.stopPropagation();
      event.preventDefault();
    };


  }
})();
