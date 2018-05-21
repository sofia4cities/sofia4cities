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
    var socket = new SockJS('/digitaltwinbroker/webservice');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/action/custom/SenseHatSpain', function (notification) {
        	//Joystick events
        	 var obj=JSON.parse(notification.body)
        	 
        	 $("#joystick").val(obj.event);
        	 if(obj.event=="joystickEventUp"){
        		 $("#sendUp").css("background-color", "red");
        		 $("#sendDown").css("background-color", "");
        		 $("#sendLeft").css("background-color", "");
        		 $("#sendRight").css("background-color", "");
        	 }if(obj.event=="joystickEventDown"){
        		 $("#sendDown").css("background-color", "red");
        		 $("#sendUp").css("background-color", "");
        		 $("#sendLeft").css("background-color", "");
        		 $("#sendRight").css("background-color", "");
        	 }if(obj.event=="joystickEventLeft"){
        		 $("#sendLeft").css("background-color", "red");
        		 $("#sendDown").css("background-color", "");
        		 $("#sendUp").css("background-color", "");
        		 $("#sendRight").css("background-color", "");
        	 }if(obj.event=="joystickEventRight"){
        		 $("#sendRight").css("background-color", "red");
        		 $("#sendDown").css("background-color", "");
        		 $("#sendLeft").css("background-color", "");
        		 $("#sendUp").css("background-color", "");
        	 }
        });
       stompClient.subscribe('/action/shadow/SenseHatSpain', function (notification) {
    	   console.log(notification);
    	   //Temp/Hum/Atm events
           var obj=JSON.parse(notification.body)
           
           $("#temperature").val(obj.status.temperature);
           $("#humidity").val(obj.status.humidity);
           $("#pressure").val(obj.status.pressure);
           
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
    stompClient.send("/event/custom", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','target':'SenseHatSpain','event':'joystickLeft','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomRightEvent() {
    stompClient.send("/event/custom", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','target':'SenseHatSpain','event':'joystickRight','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomUpEvent() {
    stompClient.send("/event/custom", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','target':'SenseHatSpain','event':'joystickUp','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomDownEvent() {
    stompClient.send("/event/custom", {'Authorization': '6e4f94e2df81435f8af135d8112a5492'}, JSON.stringify({'id':'SenseHatSpain','target':'SenseHatSpain','event':'joystickDown','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
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

