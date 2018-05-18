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
        stompClient.subscribe('/action/custom/TurbineHelsinki', function (notification) {
        	//Joystick events
        	 var obj=JSON.parse(notification.body)
        	 
        	 $("#joystick").val(obj.event);
           
        });
       stompClient.subscribe('/action/shadow/TurbineHelsinki', function (notification) {
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
    stompClient.send("/event/custom", {'Authorization': 'f0e50f5f8c754204a4ac601f29775c15'}, JSON.stringify({'id':'TurbineHelsinki','target':'TurbineHelsinki','event':'joystickEventLeft','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomRightEvent() {
    stompClient.send("/event/custom", {'Authorization': 'f0e50f5f8c754204a4ac601f29775c15'}, JSON.stringify({'id':'TurbineHelsinki','target':'TurbineHelsinki','event':'joystickEventRight','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomUpEvent() {
    stompClient.send("/event/custom", {'Authorization': 'f0e50f5f8c754204a4ac601f29775c15'}, JSON.stringify({'id':'TurbineHelsinki','target':'TurbineHelsinki','event':'joystickEventUp','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
}

function sendCustomDownEvent() {
    stompClient.send("/event/custom", {'Authorization': 'f0e50f5f8c754204a4ac601f29775c15'}, JSON.stringify({'id':'TurbineHelsinki','target':'TurbineHelsinki','event':'joystickEventDown','status':{'temperature':0.0,'humidity':0.0,'pressure':0.0}}));
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

