var stompClient = null;

var dataPoints = [];

var options =  {
	animationEnabled: true,
	theme: "light2",
	title: {
		text: "Daily Sales Data"
	},
	axisX: {
		valueFormatString: "DD MMM YYYY",
	},
	axisY: {
		title: "USD",
		titleFontSize: 24,
		includeZero: false
	},
	data: [{
		type: "spline", 
		yValueFormatString: "$#,###.##",
		dataPoints: dataPoints
	}]
};

// MAIN WHEN READY
$( document ).ready(function() {

	$.ajax({ url: "/digitaltwinbroker/sensehat/getSensehatDevices", type: 'GET',
		success: function (data) {			 
			var devices = JSON.parse(data);
			$.each(devices, function(key, object){
				$("#devices").append("<option id='"+object.identification+"' value='"+object.identification+"'>"+object.identification+"</option>");
				$("#digitaltwin_key").val(object.digitalKey);
			});
		}
	});
	
	
	
	
});

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
       stompClient.subscribe('/api/shadow/' + $("#devices").val(), function (notification) {
    	   //Temp/Hum/Atm events
           var obj=JSON.parse(notification.body)
           
           $("#temperature").val(obj.status.temperature);
           $("#humidity").val(obj.status.humidity);
           $("#pressure").val(obj.status.pressure);
           
           var m_names = new Array("Jan", "Feb", "Mar", 
        		   "Apr", "May", "Jun", "Jul", "Aug", "Sep", 
        		   "Oct", "Nov", "Dec");

		   var d = new Date();
		   var curr_date = d.getDate();
		   var curr_month = d.getMonth();
		   var curr_year = d.getFullYear();
		   var date = curr_date + " " + m_names[curr_month] 
		   + " " + curr_year;
		   
		   for (var i = 0; i < data.length; i++) {
				dataPoints.push({
					x: new Date(data[i].date),
					y: data[i].units
				});
			}
           
           dataPoints.push({
   				x: date,
   				y: obj.status.temperature
   			});
//           
//           var tempLenght = optionsTemp.data[0].dataPoints.length;
//           optionsTemp.data[0].dataPoints.push({ x: new Date(), y: obj.status.temperature });
           $("#chartTemp").CanvasJSChart(options);
        });
       stompClient.subscribe('/api/action/' + $("#devices").val(), function (notification) {
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
	 $("#sendUp").css("background-color", "");
	 $("#sendDown").css("background-color", "");
	 $("#sendLeft").css("background-color", "red");
	 $("#sendRight").css("background-color", "");
    stompClient.send("/api/sendAction", {'Authorization': $("#digitaltwin_key").val()}, JSON.stringify({'id':$("#devices").val(),'name':'joystickLeft'}));
}

function sendCustomRightEvent() {
	 $("#sendUp").css("background-color", "");
	 $("#sendDown").css("background-color", "");
	 $("#sendLeft").css("background-color", "");
	 $("#sendRight").css("background-color", "red");
    stompClient.send("/api/sendAction", {'Authorization': $("#digitaltwin_key").val()}, JSON.stringify({'id':$("#devices").val(),'name':'joystickRight'}));
}

function sendCustomUpEvent() {
	 $("#sendUp").css("background-color", "red");
	 $("#sendDown").css("background-color", "");
	 $("#sendLeft").css("background-color", "");
	 $("#sendRight").css("background-color", "");
    stompClient.send("/api/sendAction", {'Authorization': $("#digitaltwin_key").val()}, JSON.stringify({'id':$("#devices").val(),'name':'joystickUp'}));
}

function sendCustomDownEvent() {
	 $("#sendUp").css("background-color", "");
	 $("#sendDown").css("background-color", "red");
	 $("#sendLeft").css("background-color", "");
	 $("#sendRight").css("background-color", "");
    stompClient.send("/api/sendAction", {'Authorization': $("#digitaltwin_key").val()}, JSON.stringify({'id':$("#devices").val(),'name':'joystickDown'}));
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

