(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('interactionService', InteractionService);

  /** @ngInject */
  function InteractionService($log, __env, $rootScope) {
    var vm = this;
    //Gadget interaction hash table, {gadgetsource:{emiterField:"field1", targetList: [{gadgetId,overwriteField}]}}
    vm.interactionHash = {

    };

    vm.setInteractionHash = function(interactionHash){
      vm.interactionHash = interactionHash;
    };

    vm.getInteractionHash = function(){
      return vm.interactionHash;
    };

    vm.registerGadget = function (gadgetId) {
      if(!(gadgetId in vm.interactionHash)){
        vm.interactionHash[gadgetId] = [];
      }
    };

    vm.unregisterGadget = function (gadgetId) {
      //Delete from sources list
      delete vm.interactionHash[gadgetId];
      //Delete from destination list
      for (var keyGadget in vm.interactionHash) {
        var destinationList = vm.interactionHash[keyGadget];
        for (var keyDest in destinationList) {
          var destinationFieldBundle = destinationList[keyDest];
          var found = -1; //-1 not found other remove that position in targetList array
          for (var keyGDest in destinationFieldBundle.targetList) {
            var destination = destinationFieldBundle.targetList[keyGDest];
            if (destination.gadgetId == gadgetId) {
              found = keyGDest;
              break;
            }
          }
          //delete targetList entry if diferent -1
          if (found != -1) {
            destinationBundle.targetList.splice(found, 1);
          }
        }
      }
    };

    vm.registerGadgetFieldEmitter = function (gadgetId, fieldEmitter) {
      if(!(gadgetId in vm.interactionHash)){
        vm.interactionHash[gadgetId] = [];
      }
      if(!(vm.interactionHash[gadgetId].filter(function(f){return f.emiterField === fieldEmitter}).length)){
        vm.interactionHash[gadgetId].push(
          {
            targetList: [],
            emiterField: fieldEmitter
          }
        )
      }
    };

    vm.unregisterGadgetFieldEmitter = function (gadgetId, fieldEmitter) {
      var indexEmitter;
      vm.interactionHash[gadgetId].map(function (elem, index) {
        if (elem.fieldEmitter === fieldEmitter) {
          indexEmitter = index;
        }
      })
      vm.interactionHash[gadgetId].splice(found, 1);
    };

    vm.registerGadgetInteractionDestination = function (sourceGadgetId, targetGadgetId, originField, destinationField) {
      //Auto generated
      if(!(sourceGadgetId in vm.interactionHash) || !(vm.interactionHash[sourceGadgetId].filter(function(f){return f.emiterField === originField}).length)){
        vm.registerGadgetFieldEmitter(sourceGadgetId, originField);
      }
      var destinationFieldBundle = vm.interactionHash[sourceGadgetId].filter(
        function (elem) {
          return elem.emiterField == originField;
        }
      );
      destinationFieldBundle[0].targetList.push({
        gadgetId: targetGadgetId,
        overwriteField: destinationField
      })
    };

    vm.unregisterGadgetInteractionDestination = function (sourceGadgetId, targetGadgetId, originField, destinationField) {
      var destinationFieldBundle = vm.interactionHash[sourceGadgetId].filter(
        function (elem) {
          return elem.emiterField == originField;
        }
      );
      var found = -1;
      destinationFieldBundle[0].targetList.map(
        function (dest, index) {
          if (dest.overwriteField == destinationField && dest.gadgetId == targetGadgetId) {
            found = index;
          }
        }
      );
      if (found != -1) {
        destinationFieldBundle[0].targetList.splice(found, 1);
      }
    };

    //SourceFilterData: {"field1":"value1","field2":"value2","field3":"value3"}
    vm.sendBroadcastFilter = function (gadgetId, sourceFilterData) {
      var destinationList = vm.interactionHash[gadgetId];
      for (var keyDest in destinationList) {
        var destinationFieldBundle = destinationList[keyDest];
        //Target list is not empty and field came from triggered gadget data
        if (destinationFieldBundle.targetList.length > 0 && destinationFieldBundle.emiterField in sourceFilterData) {
          for (var keyGDest in destinationFieldBundle.targetList) {
            var destination = destinationFieldBundle.targetList[keyGDest];
            emitToTargets(destination.gadgetId, buildFilterEvent(destination, destinationFieldBundle, sourceFilterData, gadgetId));
          }
        }
      }
    };

    function buildFilterEvent(destination, destinationFieldBundle, sourceFilterData, gadgetEmitterId) {
      var sourceFilterDataAux = angular.copy(sourceFilterData);
      //we add first de filter event by the parent filter and then we add the chaining filter with the same filterId in order to propagate filters
      var dataFilter = [{
        "field": destination.overwriteField, 
        "value": sourceFilterData[destinationFieldBundle.emiterField].value
      }];
      delete sourceFilterDataAux[destinationFieldBundle.emiterField];
      for(var field in sourceFilterDataAux){
        dataFilter.push({
          "field": field, 
          "value": sourceFilterDataAux[field].value
        })
      }
      return {
        "type": "filter", 
        "id": gadgetEmitterId, 
        "data": dataFilter
      };
    }

    function emitToTargets(id, data) {
      $rootScope.$broadcast(id, data);
    }

  };
})();
