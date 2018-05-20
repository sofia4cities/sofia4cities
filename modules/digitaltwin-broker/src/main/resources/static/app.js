var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/digitaltwinbroker/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/api/custom/SenseHatSpain', function (notification) {
        	//Joystick events
        	 var obj=JSON.parse(notification.body)
        	 
        	 $("#joystick").val(obj.event);
           
        });
       stompClient.subscribe('/api/shadow/SenseHatSpain', function (notification) {
    	   //Temp/Hum/Atm events
           var obj=JSON.parse(notification.body)
           
           $("#temperature").val(obj.status.temperature);
           $("#humidity").val(obj.status.humidity);
           $("#pressure").val(obj.status.pressure);
           
        });
       stompClient.subscribe('/api/action/SenseHatSpain', function (notification) {
    	 console.log(notification);
    	 //Joystick events
    	 var obj=JSON.parse(notification.body)
      	 
      	 $("#joystick").val(obj.name);
      	
        });
       
       
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendCustomLeftEvent() {
    stompClient.send("/api/sendAction", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','name':'joystickLeft'}));
}

function sendCustomRightEvent() {
    stompClient.send("/api/sendAction", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','name':'joystickRight'}));
}

function sendCustomUpEvent() {
    stompClient.send("/api/sendAction", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','name':'joystickUp'}));
}

function sendCustomDownEvent() {
    stompClient.send("/api/sendAction", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','name':'joystickDown'}));
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendLeft" ).click(function() { sendCustomLeftEvent(); });
    $( "#sendRight" ).click(function() { sendCustomRightEvent(); });
    $( "#sendUp" ).click(function() { sendCustomUpEvent(); });
    $( "#sendDown" ).click(function() { sendCustomDownEvent(); });
    
});

