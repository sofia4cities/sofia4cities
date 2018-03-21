(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .service('socketService', SocketService);

  /** @ngInject */
  function SocketService($stomp, $log, __env, $timeout) {
      var vm = this;

      vm.stompClient = {};
      vm.hashRequestResponse = {};
      vm.connected = false;
      vm.initQueue = [];
      $stomp.setDebug(function (args) {
        $log.debug(args)
      });

      vm.connect = function(){
        $stomp.connect(__env.socketEndpointConnect, []).then(
          function(frame){
            if(frame.command == "CONNECTED"){
              vm.connected=true;
              while(vm.initQueue.length>0){
                var datasource = vm.initQueue.pop()
                vm.sendAndSubscribe(datasource);
              }
            }
            else{
              console.log("Error, reconnecting...")
              $timeout(vm.connect,2000);
            }
          }
        )
      }

      vm.connectAndSendAndSubscribe = function(reqrespList){
        $stomp
          .connect(__env.socketEndpointConnect, [])

          // frame = CONNECTED headers
          .then(function (frame) {
            for(var reqrest in reqrespList){
              var UUID = (new Date()).getTime();
              vm.hashRequestResponse[UUID] = reqrespList[reqrest];
              vm.hashRequestResponse[UUID].subscription = $stomp.subscribe(__env.socketEndpointSubscribe + "/" + UUID, function (payload, headers, res) {
                var answerId = headers.destination.split("/").pop();
                vm.hashRequestResponse[answerId].callback(vm.hashRequestResponse[answerId].id,payload);
                // Unsubscribe
                vm.hashRequestResponse[answerId].subscription.unsubscribe();//Unsubscribe
              })
              // Send message
              $stomp.send(__env.socketEndpointSend + "/" + UUID, reqrespList[reqrest].msg)
            }
          })
        };

      vm.sendAndSubscribe = function(datasource){
        if(vm.connected){
          var UUID = (new Date()).getTime();
          vm.hashRequestResponse[UUID] = datasource;
          vm.hashRequestResponse[UUID].subscription = $stomp.subscribe(__env.socketEndpointSubscribe + "/" + UUID, function (payload, headers, res) {
            var answerId = headers.destination.split("/").pop();
            vm.hashRequestResponse[answerId].callback(vm.hashRequestResponse[answerId].id,payload);
            // Unsubscribe
            vm.hashRequestResponse[UUID].subscription.unsubscribe();//Unsubscribe
          })

          // Send message
          $stomp.send(__env.socketEndpointSend + "/" + UUID, datasource.msg)
        }
        else{
          vm.initQueue.push(datasource);
        }
      }


      vm.disconnect = function(reqrespList){
        $stomp.disconnect().then(function () {
          $log.info('disconnected')
        })
      }
  };
})();
