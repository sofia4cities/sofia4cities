var digitalTwinApi = Java.type('com.indracompany.sofia2.digitaltwin.logic.api.DigitalTwinApi').getInstance();
var senseHatApi = Java.type('com.indracompany.sofia2.raspberry.sensehat.digitaltwin.api.SenseHatApi').getInstance();

function init(){
	
	senseHatApi.setJoystickUpListener('joystickEventUp');
	senseHatApi.setJoystickDownListener('joystickEventDown')
	senseHatApi.setJoystickLeftListener('joystickEventLeft');
	senseHatApi.setJoystickRightListener('joystickEventRight');
	senseHatApi.setJoystickMiddleListener('joystickEventMiddle');
	
	digitalTwinApi.log('Init SenseHatSpain shadow');
	
	var sensorPress = senseHatApi.getPressure();
	var sensorTemp = senseHatApi.getTemperature();
	var sensorHum = senseHatApi.getHumidity();
	
	digitalTwinApi.setStatusValue('pressure', sensorPress);
	digitalTwinApi.setStatusValue('temperature', sensorTemp);
    digitalTwinApi.setStatusValue('humidity', sensorHum);
    
    var temp = digitalTwinApi.getStatusValue('temperature');
    var hum = digitalTwinApi.getStatusValue('humidity');
    var pressure = digitalTwinApi.getStatusValue('pressure');
    
    digitalTwinApi.log('Temperature: ' + temp + ' - Humidity: ' + hum + ' - Pressure: '+ pressure);
    
    digitalTwinApi.sendUpdateShadow();
    
    digitalTwinApi.log('Send Update Shadow');
}

function main(){
	
	digitalTwinApi.log('New main execution');
	
	var sensorPress = senseHatApi.getPressure();
	var sensorTemp = senseHatApi.getTemperature();
	var sensorHum = senseHatApi.getHumidity();
	
	digitalTwinApi.setStatusValue('pressure', sensorPress);
	digitalTwinApi.setStatusValue('temperature', sensorTemp);
    digitalTwinApi.setStatusValue('humidity', sensorHum);
    
    digitalTwinApi.sendUpdateShadow();
    
    digitalTwinApi.log('Send Update Shadow');
}

var joystickEventLeft=function(event){
	digitalTwinApi.log('Received joystick event to the left');
	digitalTwinApi.sendCustomEvent('joystickEventLeft');
	senseHatApi.showTextLedMatrix(event);
}

var joystickEventRight=function(event){
	digitalTwinApi.log('Received joystick event to the right');
	digitalTwinApi.sendCustomEvent('joystickEventRight');
	senseHatApi.showTextLedMatrix(event);
}

var joystickEventUp=function(event){
	digitalTwinApi.log('Received joystick event up');
	digitalTwinApi.sendCustomEvent('joystickEventUp');
	senseHatApi.showTextLedMatrix(event);
}

var joystickEventDown=function(event){
	digitalTwinApi.log('Received joystick event down');
	digitalTwinApi.sendCustomEvent('joystickEventDown');
	senseHatApi.showTextLedMatrix(event);
}

var joystickEventMiddle=function(event){
	digitalTwinApi.log('Received joystick event to the middle');
	digitalTwinApi.sendCustomEvent('joystickEventMiddle');
	senseHatApi.showTextLedMatrix(event);
}


var onActionJoystickRight=function(data){
	digitalTwinApi.log('Received joystick action to the right');
	senseHatApi.showTextLedMatrix('Right');
}
function onActionJoystickLeft(data){
	digitalTwinApi.log('Received joystick action to the left');
	senseHatApi.showTextLedMatrix('Left');
}
var onActionJoystickUp=function(data){ 
	digitalTwinApi.log('Received joystick action up');
	senseHatApi.showTextLedMatrix('Up');
}
var onActionJoystickDown=function(data){ 
	digitalTwinApi.log('Received joystick action down');
	senseHatApi.showTextLedMatrix('Down');
}
var onActionJoystickMiddle=function(data){ 
	digitalTwinApi.log('Received joystick action to the middle');
	senseHatApi.showTextLedMatrix('Middle');
}
