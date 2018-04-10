(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('gadget', {
      templateUrl: 'app/components/view/gadgetComponent/gadget.html',
      controller: GadgetController,
      controllerAs: 'vm',
      bindings:{
        id:"<?",
        gconfig:"=?",
        gmeasures:"=?",
        gdatasourceid:"=?",
        datastatus:"=?"
      }
    });

  /** @ngInject */
  function GadgetController($log, $scope, $element, $window, $mdCompiler, $compile, datasourceSolverService, sofia2HttpService, interactionService, utilsService, leafletMarkerEvents) {
    var vm = this;
    vm.ds = [];
    vm.type = "loading";
    vm.config = {};//Gadget database config
    vm.measures = [];
    vm.status = "initial"

    //Chaining filters, used to propagate own filters to child elements
    vm.filterChaining=true;

    vm.$onInit = function(){
      //register Gadget in interaction service when gadget has id
      if(vm.id){
        interactionService.registerGadget(vm.id);
      }
      //Activate incoming events
      vm.unsubscribeHandler = $scope.$on(vm.id,eventGProcessor);
      $scope.reloadContent();
    }

    $scope.reloadContent = function(){
      /*Gadget Editor Mode*/
      if(!vm.id){
        //vm.config = vm.gconfig;//gadget config
        if(!vm.config.config){
          return;//Init editor triggered
        }
        if(typeof vm.config.config == "string"){
          vm.config.config = JSON.parse(vm.config.config);
        }
        //vm.measures = vm.gmeasures;//gadget config
        var projects = [];
        for(var index=0; index < vm.measures.length; index++){
          var jsonConfig = JSON.parse(vm.measures[index].config);
          for(var indexF = 0 ; indexF < jsonConfig.fields.length; indexF++){
            if(!utilsService.isSameJsonInArray( { op:"", field:jsonConfig.fields[indexF] },projects)){
              projects.push({op:"",field:jsonConfig.fields[indexF]});
            }
          }
          vm.measures[index].config = jsonConfig;
        }
        sofia2HttpService.getDatasourceById(vm.ds).then(
          function(datasource){
            subscriptionDatasource(datasource.data, [], projects, []);
          }
        )
      }
      else{
      /*View Mode*/
        sofia2HttpService.getGadgetConfigById(
          vm.id
        ).then(
          function(config){
            vm.config=config.data;
            vm.config.config = JSON.parse(vm.config.config);
            return sofia2HttpService.getGadgetMeasuresByGadgetId(vm.id);
          }
        ).then(
          function(measures){
            vm.measures = measures.data;

            vm.projects = [];
            for(var index=0; index < vm.measures.length; index++){
              var jsonConfig = JSON.parse(vm.measures[index].config);
              for(var indexF = 0 ; indexF < jsonConfig.fields.length; indexF++){
                if(!utilsService.isSameJsonInArray( { op:"", field:jsonConfig.fields[indexF] },vm.projects)){
                  vm.projects.push({op:"",field:jsonConfig.fields[indexF]});
                }
              }
              vm.measures[index].config = jsonConfig;
            }
            sofia2HttpService.getDatasourceById(vm.measures[0].datasource.id).then(
              function(datasource){
                subscriptionDatasource(datasource.data, [], vm.projects, []);
              }
            )
          }
        )
      }
    }

    vm.$onChanges = function(changes) {

    };

    vm.$onDestroy = function(){
      if(vm.unsubscribeHandler){
        vm.unsubscribeHandler();
        vm.unsubscribeHandler=null;
        datasourceSolverService.unregisterDatasourceTrigger(vm.measures[0].datasource);
      }
    }

    function subscriptionDatasource(datasource, filter, project, group) {

      datasourceSolverService.registerSingleDatasourceAndFirstShot(//Raw datasource no group, filter or projections
        {
          type: datasource.mode,
          name: datasource.identification,
          refresh: datasource.refresh,
          triggers: [{params:{filter:filter, group:group, project:project},emitTo:vm.id}]
        }
      );
    };

    function processDataToGadget(data){ //With dynamic loading this will change
      switch(vm.config.type){
        case "line":
        case "bar":
        case "pie":
          //Group X axis values
          var allLabelsField = [];
          for(var index=0; index < vm.measures.length; index++){
            allLabelsField = allLabelsField.concat(data.map(function(d,ind){return utilsService.getJsonValueByJsonPath(d,vm.measures[index].config.fields[0],ind)}));
          }
          allLabelsField = utilsService.sort_unique(allLabelsField);

          //Match Y values
          var allDataField = [];//Data values sort by labels
          for(var index=0; index < vm.measures.length; index++){
            var dataRawSerie = data.map(function(d,ind){return utilsService.getJsonValueByJsonPath(d,vm.measures[index].config.fields[1],ind)});
            var labelRawSerie = data.map(function(d,ind){return utilsService.getJsonValueByJsonPath(d,vm.measures[index].config.fields[0],ind)});
            var sortedArray = [];
            for(var indexf = 0; indexf < dataRawSerie.length; indexf++){
              sortedArray[allLabelsField.indexOf(labelRawSerie[indexf])] = dataRawSerie[indexf];
            }
            allDataField.push(sortedArray);
          }

          vm.labels = allLabelsField;
          vm.series = vm.measures.map (function(m){return m.config.name});

          if(vm.config.type == "pie"){
            vm.data = allDataField[0];
          }
          else{
            vm.data = allDataField;
          }

          var baseOptionsChart = {
            legend: {
              display: true, 
              labels: {
                boxWidth: 11
              }
            }, 
            maintainAspectRatio: false, 
            responsive: true, 
            responsiveAnimationDuration:500
          };

          vm.datasetOverride = vm.measures.map (function(m){return m.config.config});

          vm.optionsChart = angular.merge({},vm.config.config,baseOptionsChart);

          break;
        case 'wordcloud':
          //Get data in an array
          var arrayWordSplited = data.reduce(function(a,b){return a.concat(b.value.split(" "))},[])//data.flatMap(function(d){return getJsonValueByJsonPath(d,vm.measures[index].config.fields[0]).split(" ")})
          var hashWords = {};
          var counterArray = []
          for(var index = 0; index < arrayWordSplited.length; index++){
            var word = arrayWordSplited[index];
            if(word in hashWords){
              counterArray[hashWords[word]].count++;
            }
            else{
              hashWords[word]=counterArray.length;
              counterArray.push({text:word,count:1});
            }
          }

          vm.counterArray = counterArray.sort(function(a, b){
            return b.count - a.count;
          })
          redrawWordCloud();
          $scope.$on("$resize",redrawWordCloud);
          break;
        case "map":
          vm.center = vm.center || vm.config.config.center;
          vm.markers = data.map(
            function(d){
              return {
                lat: utilsService.getJsonValueByJsonPath(d,vm.measures[0].config.fields[0],0),
                lng: utilsService.getJsonValueByJsonPath(d,vm.measures[0].config.fields[1],1),

                message: vm.measures[0].config.fields.slice(3).reduce(
                  function(a, b){
                    return a + "<b>" + b + ":</b>&nbsp;" + utilsService.getJsonValueByJsonPath(d,b) + "<br/>";
                  }
                  ,""
                ),
                id: utilsService.getJsonValueByJsonPath(d,vm.measures[0].config.fields[2],2)
              }
            }
          )
          $scope.events = {
            markers: {
                enable: leafletMarkerEvents.getAvailableEvents(),
            }
          };
          
          //Init map events
          var eventName = 'leafletDirectiveMarker.lmap' + vm.id + '.click';
          $scope.$on(eventName, vm.clickMarkerMapEventProcessorEmitter);

          redrawLeafletMap();
          $scope.$on("$resize",redrawLeafletMap);
          break;
      }

      vm.type = vm.config.type;//Activate gadget
      utilsService.forceRender($scope);
    }

    function redrawWordCloud(){
      var element = $element[0];
      var height = element.offsetHeight;
      var width = element.offsetWidth;
      var maxCount = vm.counterArray[0].count;
      var minCount = vm.counterArray[vm.counterArray.length - 1].count;
      var maxWordSize = width * 0.15;
      var minWordSize = maxWordSize / 5;
      var spread = maxCount - minCount;
      if (spread <= 0) spread = 1;
      var step = (maxWordSize - minWordSize) / spread;
      vm.words = vm.counterArray.map(function(word) {
          return {
              text: word.text,
              size: Math.round(maxWordSize - ((maxCount - word.count) * step)),
              tooltipText: word.count + ' ocurrences'
          }
      })
      vm.width = width;
      vm.height = height;
    }

    function redrawLeafletMap(){
      var element = $element[0];
      var height = element.offsetHeight;
      var width = element.offsetWidth;
      vm.width = width;
      vm.height = height;
    }

    function eventGProcessor(event,dataEvent){
      if(dataEvent.type === "data" && dataEvent.data.length===0){
        vm.type="nodata";
        vm.status = "ready";
      }
      else{
        switch(dataEvent.type){
          case "data":
            switch(dataEvent.name){
              case "refresh":
                if(vm.status === "initial" || vm.status === "ready"){
                  processDataToGadget(dataEvent.data);
                }
                else{
                  console.log("Ignoring refresh event, status " + vm.status);
                }
                break;
              case "add":
                //processDataToGadget(data);
                break;
              case "filter":
                if(vm.status === "pending"){
                  processDataToGadget(dataEvent.data);
                  vm.status = "ready";
                }
                break;
              case "drillup":
                //processDataToGadget(data);
                break;
              case "drilldown":
                //processDataToGadget(data);
                break;
              default:
                console.error("Not allowed data event: " + dataEvent.name);
                break;
            } 
            break;
          case "filter":
            vm.status = "pending";
            //vm.type = "loading";
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

    //Chartjs click event
    vm.clickChartEventProcessorEmitter = function(points, evt){
      var originField;
      var originValue;
      switch(vm.config.type){
        case "line":
          //originField = vm.measures[0].config.fields[0];
          //originValue = points[0]._model.label;
          break;
        case "bar":
          //find serie x field if there are diferent x field in measures
          for(var index in vm.data){
            if(vm.data[index][points[0]._index]){
              originField = vm.measures[index].config.fields[0];
              break;
            }
          }
          originValue = points[0]._model.label;
          break;
        case "pie":
          originField = vm.measures[0].config.fields[0];
          originValue = points[0]._model.label;
          break;
      }
      sendEmitterEvent(originField,originValue);
    }

    //leafletjs click marker event, by Point Id
    vm.clickMarkerMapEventProcessorEmitter = function(event, args){
      var originField = vm.measures[0].config.fields[2];
      var originValue = args.model.id;
      sendEmitterEvent(originField,originValue);
    }

    function sendEmitterEvent(originField,originValue){
      if(vm.filterChaining){
        var filterStt = angular.copy(vm.datastatus)||{}
      }
      else{
        var filterStt = {}
      }
      filterStt[originField]=originValue;
      interactionService.sendBroadcastFilter(vm.id,filterStt);
    }
  }
})();
