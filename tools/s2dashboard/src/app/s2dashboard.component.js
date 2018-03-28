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
  function MainController($log, $scope, $mdSidenav, $mdDialog, $timeout, sofia2HttpService, interactionService) {
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

          $timeout(function(){
            interactionService.registerGadgetFieldEmitter("24286b82-e428-489e-9429-f4c539a7c8f2","cuisine");
            interactionService.registerGadgetFieldEmitter("0d6fb521-49e1-4c34-85d8-c4b30b3795bd","borough");
            interactionService.registerGadgetInteractionDestination("24286b82-e428-489e-9429-f4c539a7c8f2", "b4506302-4a6f-4b8a-ac77-7988f064b71e", "cuisine", "cuisine");
            //interactionService.registerGadgetInteractionDestination("24286b82-e428-489e-9429-f4c539a7c8f2", "e179daad-c908-49e8-8d96-3b2220866c53", "cuisine", "cuisine");
            interactionService.registerGadgetInteractionDestination("24286b82-e428-489e-9429-f4c539a7c8f2", "0d6fb521-49e1-4c34-85d8-c4b30b3795bd", "cuisine", "cuisine");
            interactionService.registerGadgetInteractionDestination("0d6fb521-49e1-4c34-85d8-c4b30b3795bd", "e179daad-c908-49e8-8d96-3b2220866c53", "borough", "borough");
          },3000);
        }
      )
      /*
      vm.dashboard = {
        header: {
          title: "My new s4c Dashboard",
          enable: true,
          height: 64,
          logo: {
            height: 64
          }
        },
        navigation:{
          showBreadcrumbIcon: true,
          showBreadcrumb: true
        },
        pages: [],
        gridOptions: {
          gridType: 'fit',
          compactType: 'none',
          margin: 0,
          outerMargin: false,
          mobileBreakpoint: 640,
          minCols: 20,
          maxCols: 100,
          minRows: 20,
          maxRows: 100,
          maxItemCols: 5000,
          minItemCols: 1,
          maxItemRows: 5000,
          minItemRows: 1,
          maxItemArea: 25000,
          minItemArea: 1,
          defaultItemCols: 4,
          defaultItemRows: 4,
          fixedColWidth: 250,
          fixedRowHeight: 250,
          enableEmptyCellClick: false,
          enableEmptyCellContextMenu: false,
          enableEmptyCellDrop: false,
          enableEmptyCellDrag: false,
          emptyCellDragMaxCols: 5000,
          emptyCellDragMaxRows: 5000,
          draggable: {
            delayStart: 100,
            enabled: true,
            ignoreContent: true,
            dragHandleClass: 'drag-handler'
          },
          resizable: {
            delayStart: 0,
            enabled: true
          },
          swap: false,
          pushItems: true,
          disablePushOnDrag: false,
          disablePushOnResize: false,
          pushDirections: {north: true, east: true, south: true, west: true},
          pushResizeItems: false,
          displayGrid: 'always',
          disableWindowResize: false,
          disableWarnings: false,
          scrollToNewItems: true
        }
      }*/

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
        newElem.id = type;
        newElem.content = type;
        newElem.type = type;
        newElem.header = {
          enable: true,
          title: {
            icon: "",
            iconColor: "hsl(0, 0%, 100%)",
            text: type,
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
