(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('s2dashboard', {
      templateUrl: 'app/s2dashboard.html',
      controller: MainController,
      controllerAs: 'vm',
      bindings:{
        editmode : "=",
        selectedpage : "&",
        id: "@",
        public: "="
      }
    });

  /** @ngInject */
  function MainController($log, $scope, $mdSidenav, $mdDialog, $timeout, $window, sofia2HttpService, interactionService) {
    var vm = this;
    vm.$onInit = function () {
      setTimeout(function () {
        vm.sidenav = $mdSidenav('left');
      }, 100);
      vm.selectedpage=0;

      /*Rest api call to get dashboard data*/
      sofia2HttpService.getDashboardModel(vm.id).then(
        function(model){
          vm.dashboard = model.data;

          vm.dashboard.gridOptions.resizable.stop = sendResizeToGadget;

          vm.dashboard.gridOptions.enableEmptyCellDrop = true;
          vm.dashboard.gridOptions.emptyCellDropCallback = dropElementEvent.bind(this);

          //If interaction hash then recover connections
          if(vm.dashboard.interactionHash){
            interactionService.setInteractionHash(vm.dashboard.interactionHash);
          }
        }
      ).catch(
        function(){
          $window.location.href = "/controlpanel/login";
        }
      )

      function showAddGadgetDialog(type,config,layergrid){
        function AddGadgetController($scope, $mdDialog, sofia2HttpService, type, config, layergrid) {
          $scope.type = type;
          $scope.config = config;
          $scope.layergrid = layergrid;

          $scope.gadgets = [];

          $scope.hide = function() {
            $mdDialog.hide();
          };

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

          $scope.loadGadgets = function() {
            return sofia2HttpService.getUserGadgetsByType($scope.type).then(
              function(gadgets){
                $scope.gadgets = gadgets.data;
              }
            );
          };

          $scope.addGadget = function() {
            $scope.config.type = $scope.type;
            $scope.config.id = $scope.gadget.id;
            $scope.config.header.title.text = $scope.gadget.identification;
            $scope.layergrid.push($scope.config);
            $mdDialog.cancel();
          };
        }

        $mdDialog.show({
          controller: AddGadgetController,
          templateUrl: 'app/partials/edit/addGadgetDialog.html',
          parent: angular.element(document.body),
          clickOutsideToClose:true,
          fullscreen: false, // Only for -xs, -sm breakpoints.
          openFrom: '.sidenav-fab',
          closeTo: angular.element(document.querySelector('.sidenav-fab')),
          locals: {
            type: type,
            config: config,
            layergrid: layergrid
          }
        })
        .then(function() {

        }, function() {
          $scope.status = 'You cancelled the dialog.';
        });
      }


      function dropElementEvent(e,newElem){
        var type = e.dataTransfer.getData("type");
        newElem.id = type + "_" + (new Date()).getTime();
        newElem.content = type;
        newElem.type = type;
        newElem.header = {
          enable: true,
          title: {
            icon: "",
            iconColor: "hsl(0, 0%, 100%)",
            text: type + "_" + (new Date()).getTime(),
            textColor: "hsl(0, 0%, 100%)"
          },
          backgroundColor: "hsl(200, 23%, 64%)",
          height: "37"
        }
        newElem.backgroundColor ="white";
        newElem.padding = 0;
        newElem.border = {
          color: "hsl(0, 0%, 82%)",
          width: 1,
          radius: 1
        }
        if(type == 'livehtml'){
          var layerGrid = vm.dashboard.pages[vm.selectedpage].layers[vm.dashboard.pages[vm.selectedpage].selectedlayer];
          layerGrid.gridboard.push(newElem);
          $scope.$applyAsync();
        }
        else{
          showAddGadgetDialog(type,newElem,vm.dashboard.pages[vm.selectedpage].layers[vm.dashboard.pages[vm.selectedpage].selectedlayer].gridboard);
        }
      };


      function sendResizeToGadget(item, itemComponent) {
        $timeout(
          function(){
            $scope.$broadcast("$resize", "");
          },100
        );
      }


      /*var page = {};
      var layer = {};

      layer.gridboard = [
        {
          cols: 8,
          rows: 7,
          y: 0,
          x: 0,
          id: "1",
          type: "livehtml",
          content: "<h1> LiveHTML Text </h1>",
          header: {
            enable: true,
            title: {
              icon: "",
              iconColor: "none",
              text: "Leaflet Map Gadget test",
              textColor: "none"
            },
            backgroundColor: "none",
            height: 64
          },
          backgroundColor: "initial",
          padding: 0,
          border: {
            color: "black",
            width: 1,
            radius: 5
          }
        }
      ];

      layer.title = "baseLayer";

      page.title = "New Page";
      page.icon = "android"
      page.background = {}
      page.background.file = []

      page.layers = [layer];
      */
     /* Edit mode only */
      /*page.selectedlayer=0
      page.combinelayers=!vm.editmode;

      var page2 = JSON.parse(JSON.stringify(page));
      page2.icon = "bug_report";
      page2.title = "New Page2";

      vm.dashboard.pages.push(page);
      vm.dashboard.pages.push(page2);
      */

    };

    vm.checkIndex = function(index){
      return vm.selectedpage === index;
    }

    vm.setIndex = function(index){
      vm.selectedpage = index;
    }
  }
})();
