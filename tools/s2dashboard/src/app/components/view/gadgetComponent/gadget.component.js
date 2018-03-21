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
        gdatasourceid:"=?"
      }
    });

  /** @ngInject */
  function GadgetController($log, $scope, $element, $window, $mdCompiler, $compile, datasourceSolverService, sofia2HttpService) {
    var vm = this;
    vm.ds = [];
    vm.type = "loading";
    vm.config = {};//Gadget database config
    vm.measures = [];

    vm.$onInit = function(){
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
            if(!isSameJsonInArray( { op:"", field:jsonConfig.fields[indexF] },projects)){
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

            var projects = [];
            for(var index=0; index < vm.measures.length; index++){
              var jsonConfig = JSON.parse(vm.measures[index].config);
              for(var indexF = 0 ; indexF < jsonConfig.fields.length; indexF++){
                if(!isSameJsonInArray( { op:"", field:jsonConfig.fields[indexF] },projects)){
                  projects.push({op:"",field:jsonConfig.fields[indexF]});
                }
              }
              vm.measures[index].config = jsonConfig;
            }
            sofia2HttpService.getDatasourceById(vm.measures[0].datasource.id).then(
              function(datasource){
                subscriptionDatasource(datasource.data, [], projects, []);
              }
            )
          }
        )
      }
    }

    function isSameJsonInArray(json,arrayJson){
      for(var index = 0; index < arrayJson.length; index ++){
        var equals = true;
        for(var key in arrayJson[index]){
          if(arrayJson[index][key] != json[key]){
            equals = false;
            break;
          }
        }
        if(equals){
          return true;
        }
      }
      return false;
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
      vm.unsubscribeHandler = $scope.$on(vm.id,function(event,data){
        if(data.length!=0){
          processDataToGadget(data);
        }
        else{
          vm.type="nodata";
        }
      });

      datasourceSolverService.registerSingleDatasourceAndFirstShot(//Raw datasource no group, filter or projections
        {
          type: datasource.mode,
          name: datasource.identification,
          refresh: datasource.refresh,
          triggers: [{params:{filter:filter, group:group, project:project},emiter:vm.id}]
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
            allLabelsField = allLabelsField.concat(data.map(function(d,ind){return getJsonValueByJsonPath(d,vm.measures[index].config.fields[0],ind)}));
          }
          allLabelsField = sort_unique(allLabelsField);

          //Match Y values
          var allDataField = [];//Data values sort by labels
          for(var index=0; index < vm.measures.length; index++){
            var dataRawSerie = data.map(function(d,ind){return getJsonValueByJsonPath(d,vm.measures[index].config.fields[1],ind)});
            var labelRawSerie = data.map(function(d,ind){return getJsonValueByJsonPath(d,vm.measures[index].config.fields[0],ind)});
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
          vm.optionsChart = {legend: {display: true}, maintainAspectRatio: false, responsive: true, responsiveAnimationDuration:500};
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
          vm.center = vm.config.config.center;
          vm.markers = data.map(
            function(d){
              return {
                lat: getJsonValueByJsonPath(d,vm.measures[0].config.fields[0],0),
                lng: getJsonValueByJsonPath(d,vm.measures[0].config.fields[1],1),
                message: vm.measures[0].config.fields.slice(2).reduce(
                  function(a, b){
                    return a + "<b>" + b + ":</b>&nbsp;" + getJsonValueByJsonPath(d,b) + "<br/>";
                  }
                  ,""
                )
              }
            }
          )
          redrawLeafletMap();
          $scope.$on("$resize",redrawLeafletMap);
          break;
      }

      vm.type = vm.config.type;//Activate gadget
      if(!$scope.$$phase) {
        $scope.$applyAsync();
      }
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

    //Access json by string dot path
    function multiIndex(obj,is,pos) {  // obj,['1','2','3'] -> ((obj['1'])['2'])['3']
      if(is.length && !(is[0] in obj)){
        return obj[is[is.length-1]];
      }
      return is.length ? multiIndex(obj[is[0]],is.slice(1),pos) : obj
    }

    function getJsonValueByJsonPath(obj,is,pos) {
      //special case for array access, return key is 0, 1
      var matchArray = is.match(/\[[0-9]\]*$/);
      if(matchArray){
        //Get de match in is [0] and get return field name
        return obj[pos];
      }
      return multiIndex(obj,is.split('.'))
    }

    //array transform to sorted and unique values
    function sort_unique(arr) {
      if (arr.length === 0) return arr;
      var sortFn;
      if(typeof arr[0] === "string"){//String sort
        sortFn = function (a, b) {
          if(a < b) return -1;
          if(a > b) return 1;
          return 0;
        }
      }
      else{//Number and date sort
        sortFn = function (a, b) {
          return a*1 - b*1;
        }
      }
      arr = arr.sort(sortFn);
      var ret = [arr[0]];
      for (var i = 1; i < arr.length; i++) { //Start loop at 1: arr[0] can never be a duplicate
        if (arr[i-1] !== arr[i]) {
          ret.push(arr[i]);
        }
      }
      return ret;
    }
  }
})();
