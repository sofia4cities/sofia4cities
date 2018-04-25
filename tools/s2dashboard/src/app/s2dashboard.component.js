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
  function MainController($log, $scope, $mdSidenav, $mdDialog, $timeout, $window, sofia2HttpService, interactionService, gadgetManagerService) {
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
          vm.dashboard.gridOptions.displayGrid = "none";
          if(!vm.editmode){           
            vm.dashboard.gridOptions.draggable.enabled = false;
            vm.dashboard.gridOptions.resizable.enabled = false;
            vm.dashboard.gridOptions.enableEmptyCellDrop = false;
          }
          gadgetManagerService.setDashboardModelAndPage(vm.dashboard,vm.selectedpage);
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


      function showAddGadgetTemplateDialog(type,config,layergrid){
        function AddGadgetController($scope, $mdDialog, sofia2HttpService, type, config, layergrid) {
          $scope.type = type;
          $scope.config = config;
          $scope.layergrid = layergrid;

         
          $scope.templates = [];

          $scope.hide = function() {
            $mdDialog.hide();
          };

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

         
          $scope.loadTemplates = function() {
            return sofia2HttpService.getUserGadgetTemplate().then(
              function(templates){
                $scope.templates = templates.data;
              }
            );
          };

          $scope.useTemplate = function() {    
                 
            $scope.config.type = $scope.type;
            $scope.config.content=$scope.template.template          
            showAddGadgetTemplateParameterDialog($scope.type,$scope.config,$scope.layergrid);
            $mdDialog.hide();
          };
          $scope.noUseTemplate = function() {
            $scope.config.type = $scope.type;        
            $scope.layergrid.push($scope.config);
            $mdDialog.cancel();
          };

        }
        $mdDialog.show({
          controller: AddGadgetController,
          templateUrl: 'app/partials/edit/addGadgetTemplateDialog.html',
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



      function showAddGadgetTemplateParameterDialog(type,config,layergrid){
        function AddGadgetController($scope, $mdDialog,$mdCompiler, sofia2HttpService, type, config, layergrid) {
          var agc = this;
          agc.$onInit = function () {
            $scope.loadDatasources();
            $scope.getPredefinedParameters();
          }
         
          $scope.type = type;
          $scope.config = config;
          $scope.layergrid = layergrid;
          $scope.datasource;
          $scope.datasources = [];
          $scope.datasourceFields = [];
          $scope.parameters = [];
         
          $scope.templates = [];

          $scope.hide = function() {
            $mdDialog.hide();
          };

          $scope.cancel = function() {
            $mdDialog.cancel();
          };

         
          $scope.loadDatasources = function(){
            return sofia2HttpService.getDatasources().then(
              function(response){
                $scope.datasources=response.data;
                
              },
              function(e){
                console.log("Error getting datasources: " +  JSON.stringify(e))
              }
            );
          };
    
          $scope.iterate=  function (obj, stack, fields) {
            for (var property in obj) {
                 if (obj.hasOwnProperty(property)) {
                     if (typeof obj[property] == "object") {
                      $scope.iterate(obj[property], stack + (stack==""?'':'.') + property, fields);
              } else {
                         fields.push({field:stack + (stack==""?'':'.') + property, type:typeof obj[property]});
                     }
                 }
              }    
              return fields;
           }

          /**method that finds the tags in the given text*/
          function searchTag(regex,str){
            var m;
            var found=[];
            while ((m = regex.exec(str)) !== null) {  
                if (m.index === regex.lastIndex) {
                    regex.lastIndex++;
                }
                m.forEach(function(item, index, arr){			
                found.push(arr[0]);			
              });  
            }
            return found;
          }
          /**method that finds the name attribute and returns its value in the given tag */
          function searchTagContentName(regex,str){
            var m;
            var content;
            while ((m = regex.exec(str)) !== null) {  
                if (m.index === regex.lastIndex) {
                    regex.lastIndex++;
                }
                m.forEach(function(item, index, arr){			
                  content = arr[0].match(/"([^"]+)"/)[1];			
              });  
            }
            return content;
          }
          /**method that finds the options attribute and returns its values in the given tag */
          function searchTagContentOptions(regex,str){
            var m;
            var content=" ";
            while ((m = regex.exec(str)) !== null) {  
                if (m.index === regex.lastIndex) {
                    regex.lastIndex++;
                }
                m.forEach(function(item, index, arr){			
                  content = arr[0].match(/"([^"]+)"/)[1];			
              });  
            }
          
            return  content.split(',');
          }

          /**we look for the parameters in the source code to create the form */
          $scope.getPredefinedParameters = function(){
            var str =  $scope.config.content;
           	var regexTag =  /<![\-\-\s\w\>\=\"\'\,\:\+\_\/]*\-->/g;
		        var regexName = /name\s*=\s*\"[\s\w\>\=\-\'\+\_\/]*\s*\"/g;
            var regexOptions = /options\s*=\s*\"[\s\w\>\=\-\'\:\,\+\_\/]*\s*\"/g;
		        var found=[];
	        	found = searchTag(regexTag,str);	
        
        
            for (var i = 0; i < found.length; i++) {			
              var tag = found[i];
              if(tag.replace(/\s/g, '').search('type="text"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){	
                $scope.parameters.push({label:searchTagContentName(regexName,tag),value:"parameterTextLabel", type:"labelsText"});
              }else if(tag.replace(/\s/g, '').search('type="number"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
                $scope.parameters.push({label:searchTagContentName(regexName,tag),value:0, type:"labelsNumber"});              
              }else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
                $scope.parameters.push({label:searchTagContentName(regexName,tag),value:"parameterDsLabel", type:"labelsds"});               
              }else if(tag.replace(/\s/g, '').search('type="ds_parameter"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
                $scope.parameters.push({label:searchTagContentName(regexName,tag),value:"parameterNameDsLabel", type:"labelsdspropertie"});               
              }else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('select-s4c')>=0){
                var optionsValue = searchTagContentOptions(regexOptions,tag); 
                $scope.parameters.push({label:searchTagContentName(regexName,tag),value:"parameterSelectLabel",type:"selects", optionsValue:optionsValue});	              
              }
             } 
            }
        

            /**find a value for a given parameter */
            function findValueForParameter(label){
                for (var index = 0; index <  $scope.parameters.length; index++) {
                  var element =  $scope.parameters[index];
                  if(element.label===label){
                    return element.value;
                  }
                }
            }
        
            /**Parse the parameter of the data source so that it has array coding*/
            function parseArrayPosition(str){
              var regex = /\.[\d]+/g;
              var m;              
              while ((m = regex.exec(str)) !== null) {                
                  if (m.index === regex.lastIndex) {
                      regex.lastIndex++;
                  } 
                  m.forEach( function(item, index, arr){             
                    var index = arr[0].substring(1,arr[0].length)
                    var result =  "["+index+"]";
                    str = str.replace(arr[0],result) ;
                  });
              }
              return str;
            }

            /** this function Replace parameteres for his selected values*/
            function parseProperties(){
              var str =  $scope.config.content;
              var regexTag =  /<![\-\-\s\w\>\=\"\'\,\:\+\_\/]*\-->/g;
              var regexName = /name\s*=\s*\"[\s\w\>\=\-\'\+\_\/]*\s*\"/g;
              var regexOptions = /options\s*=\s*\"[\s\w\>\=\-\'\:\,\+\_\/]*\s*\"/g;
              var found=[];
              found = searchTag(regexTag,str);	
          
              var parserList=[];
              for (var i = 0; i < found.length; i++) {
                var tag = found[i];			
               
                if(tag.replace(/\s/g, '').search('type="text"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){                 
                  parserList.push({tag:tag,value:findValueForParameter(searchTagContentName(regexName,tag))});   
                }else if(tag.replace(/\s/g, '').search('type="number"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
                  parserList.push({tag:tag,value:findValueForParameter(searchTagContentName(regexName,tag))});   
                }else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){                
                  var field = parseArrayPosition(findValueForParameter(searchTagContentName(regexName,tag)).field);                               
                  parserList.push({tag:tag,value:"{{ds[0]."+field+"}}"});        
                }else if(tag.replace(/\s/g, '').search('type="ds_parameter"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){                
                  var field = parseArrayPosition(findValueForParameter(searchTagContentName(regexName,tag)).field);                               
                  parserList.push({tag:tag,value:field});        
                }else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('select-s4c')>=0){                
                  parserList.push({tag:tag,value:findValueForParameter(searchTagContentName(regexName,tag))});  
                }
              } 
              //Replace parameteres for his values
              for (var i = 0; i < parserList.length; i++) {
                str = str.replace(parserList[i].tag,parserList[i].value);
              }
              return str;
            }
          
          



      
          $scope.loadDatasourcesFields = function(){
            
            if($scope.config.datasource!=null && $scope.config.datasource.id!=null && $scope.config.datasource.id!=""){
                 return sofia2HttpService.getsampleDatasources($scope.config.datasource.id).then(
                  function(response){
                    $scope.datasourceFields=$scope.iterate(response.data[0],"", []);
                  },
                  function(e){
                    console.log("Error getting datasourceFields: " +  JSON.stringify(e))
                  }
                );
              }
              else 
              {return null;}
        }


          $scope.save = function() { 
            $scope.config.type = $scope.type;
            $scope.config.content=parseProperties();            
            $scope.layergrid.push($scope.config);
            $mdDialog.cancel();
          };
        
        }
        $mdDialog.show({
          controller: AddGadgetController,
          templateUrl: 'app/partials/edit/addGadgetTemplateParameterDialog.html',
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
            iconColor: "hsl(220, 23%, 20%)",
            text: type + "_" + (new Date()).getTime(),
            textColor: "hsl(220, 23%, 20%)"
          },
          backgroundColor: "hsl(0, 0%, 100%)",
          height: "50"
        }
        newElem.backgroundColor ="white";
        newElem.padding = 0;
        newElem.border = {
          color: "hsl(0°, 0%, 80%)",
          width: 1,
          radius: 5
        }
        if(type == 'livehtml'){         
          showAddGadgetTemplateDialog(type,newElem,vm.dashboard.pages[vm.selectedpage].layers[vm.dashboard.pages[vm.selectedpage].selectedlayer].gridboard); 
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
    };

    vm.checkIndex = function(index){
      return vm.selectedpage === index;
    }

    vm.setIndex = function(index){
      vm.selectedpage = index;
    }
  }
})();
