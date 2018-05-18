var digitalTwinApi = Java.type('com.indracompany.sofia2.digitaltwin.logic.api.DigitalTwinApi').getInstance();
var hola='hola';
function init(){
	
	digitalTwinApi.log('Init SenseHatSpain shadow');
	
    digitalTwinApi.setStatusValue('temperature', 0.0);
    digitalTwinApi.setStatusValue('humidity', 0.0);
    digitalTwinApi.setStatusValue('pressure', 0.0);
    
    var temp = digitalTwinApi.getStatusValue('temperature');
    var hum = digitalTwinApi.getStatusValue('humidity');
    var pressure = digitalTwinApi.getStatusValue('pressure');
    
    digitalTwinApi.log('Temperature: ' + temp + " - Humidity: " + hum + " - Pressure: "+ pressure);
    
    digitalTwinApi.sendUpdateShadow();
    
    digitalTwinApi.log('Send Update Shadow');
}
function main(){
	
	digitalTwinApi.log('New main execution');
	
	digitalTwinApi.setStatusValue('temperature', 0.0);
    digitalTwinApi.setStatusValue('humidity', 0.0);
    digitalTwinApi.setStatusValue('pressure', 0.0);
    
    digitalTwinApi.sendUpdateShadow();
    
    digitalTwinApi.log('Send Update Shadow');
}

var joystickEventLeft=function(event){
	digitalTwinApi.log('Received joystick event to the left');
	digitalTwinApi.sendCustomEvent('joystickEventLeft');
	digitalTwinApi.showTextLedMatrix(event);
}

var joystickEventRight=function(event){
	digitalTwinApi.log('Received joystick event to the right');
	digitalTwinApi.sendCustomEvent('joystickEventRight');
	digitalTwinApi.showTextLedMatrix(event);
}

var joystickEventUp=function(event){
	digitalTwinApi.log('Received joystick event up');
	digitalTwinApi.sendCustomEvent('joystickEventUp');
	digitalTwinApi.showTextLedMatrix(event);
}

var joystickEventDown=function(event){
	digitalTwinApi.log('Received joystick event down');
	digitalTwinApi.sendCustomEvent('joystickEventDown');
	digitalTwinApi.showTextLedMatrix(event);
}

var joystickEventMiddle=function(event){
	digitalTwinApi.log('Received joystick event to the middle');
	digitalTwinApi.sendCustomEvent('joystickEventMiddle');
	digitalTwinApi.showTextLedMatrix(event);
}

var onActionJoystickRight=function(data){
	digitalTwinApi.log('Received joystick action to the right');
	digitalTwinApi.showTextLedMatrix('Right');
}
function onActionJoystickLeft(data){
	digitalTwinApi.log('Received joystick action to the left');
	digitalTwinApi.showTextLedMatrix('Left');
}
var onActionJoystickUp=function(data){ 
	digitalTwinApi.log('Received joystick action up');
	digitalTwinApi.showTextLedMatrix('Up');
}
var onActionJoystickDown=function(data){ 
	digitalTwinApi.log('Received joystick action down');
	digitalTwinApi.showTextLedMatrix('Down');
}
var onActionJoystickMiddle=function(data){ 
	digitalTwinApi.log('Received joystick action to the middle');
	digitalTwinApi.showTextLedMatrix('Middle');
}
