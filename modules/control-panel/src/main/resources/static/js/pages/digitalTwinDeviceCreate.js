
var DigitalTwinCreateController = function() {
	
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Digital Twin Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	var hasId = false; // instance
	var AceEditor;


	// CONTROLLER PRIVATE FUNCTIONS	--------------------------------------------------------------------------------
	
	
	$("#createBtn").on('click',function(){
		if($("#identification").val()!='' && $("#identification").val()!=undefined){
			$("#logic").val(JSON.stringify(ace.edit("aceEditor").getValue().trim()));
			$("#typeSelected").val($("#typeDigitalTwin").val());
			DigitalTwinCreateController.submitform();
		}else{
			$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: digitalTwinCreateJson.validations.schema});
			return false;
		}
	});
	
	$("#updateBtn").on('click',function(){
		if($("#identification").val()!='' && $("#identification").val()!=undefined){
			$("#logic").val(JSON.stringify(ace.edit("aceEditor").getValue().trim()));
			$("#typeSelected").val($("#typeDigitalTwin").val());
			DigitalTwinCreateController.submitform();
		}else{
			$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: digitalTwinCreateJson.validations.schema});
			return false;
		}
		
	});
	
	// CHECK IF JSON STRING WHEN JSON PARSE IS OK OR NOT, IF THE JSON IS MALFORMED THE JSON.parse() is broken.
	var IsJsonString = function(str) {
		try {
			JSON.parse(str);
		}
		catch (e) {	return false; }
		
		return true;
	}
	
	
	// DELETE DIGITAL TWIN TYPE
	var deleteDigitalTwinDeviceConfirmation = function(digitalTwinDeviceId){
		console.log('deleteDigitalTwinDeviceConfirmation() -> formId: '+ digitalTwinDeviceId);
		
		// no Id no fun!
		if ( !digitalTwinDeviceId ) {$.alert({title: 'ERROR!', type: 'red' , theme: 'dark', content: digitalTwinCreateJson.validations.validform}); return false; }
		
		logControl ? console.log('deleteDigitalTwinDeviceConfirmation() -> formAction: ' + $('.delete-digital').attr('action') + ' ID: ' + $('#delete-digitaltwindeviceId').attr('digitaltwindeviceId')) : '';
		
		// call digital twin device Confirm at header. 
		HeaderController.showConfirmDialogDigitalTwinDevice('delete_digitaltwindevice_form');	
	}
	
	// REDIRECT URL
	var navigateUrl = function(url){
		window.location.href = url; 
	}
	
	$("#button3").on('click', function(){
		
		$.ajax({
			url : "/controlpanel/digitaltwindevices/generateToken",
			type : 'GET',
			dataType: 'text', 
			contentType: 'text/plain',
			mimeType: 'text/plain',
			success : function(data) {
				if (data!="" && data != undefined) {
					$("#apiKey").val(data);
				} else {
					$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'error'}); 
				}
			},
			error : function(data, status, er) {
				$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: er}); 
			}
		});
	})
	
	var changeDigitalTwinType = function(type){
		
		$.ajax({
			url : "/controlpanel/digitaltwindevices/getLogicFromType/"+type,
			type : 'GET',
			dataType: 'text',
			contentType: 'text/plain',
			mimeType: 'text/plain',
			success : function(data) {
				if (data!="" && data != undefined) {
					AceEditor = ace.edit("aceEditor");
					AceEditor.setValue(data);
				} else {
					$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'error'}); 
				}
			},
			error : function(data, status, er) {
				$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: er}); 
			}
		});
	}

	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return digitalTwinCreateJson = Data;
		},

		// INIT() CONTROLLER INIT CALLS
		init: function(){
			
			logControl ? console.log(LIB_TITLE + ': init()') : '';
			
			// PROTOTYPEs
			// ARRAY PROTOTYPE FOR CHECK UNIQUE PROPERTIES.
			Array.prototype.unique = function() {
				return this.filter(function (value, index, self) { 
					return self.indexOf(value) === index;
				});
			};
			
			// ARRAY PROTROTYPE FOR REMOVE ELEMENT (not object) BY VALUE
			Array.prototype.remove = function() {
				var what, a = arguments, L = a.length, ax;				
				while (L && this.length) {
					what = a[--L];				
					while ((ax = this.indexOf(what)) !== -1) {
						console.log('AX: ' + ax);
						this.splice(ax, 1);
					}
				}
				return this;
			};
			
			//LOAD DIGITAL TWIN TYPES 
			logControl ? console.log('|---> Load Digital Twin Types') : '';
			if($("#typesDigitalTwin").val()!="" && $("#typesDigitalTwin").val()!=undefined){
				var types = $("#typesDigitalTwin").val().replace("[","").replace("]","").split(",");
				$.each(types, function(key, object){
					$("#typeDigitalTwin").append("<option id='"+object+"' value='"+object+"'>"+object+"</option>");
				});
			}else{
				$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: digitalTwinCreateJson.validations.types}); 
				return false;
			}
			
			// INSERT MODE ACTIONS  (ontologyCreateReg.actionMode = NULL ) 
			if ( digitalTwinCreateJson.actionMode === null){
				logControl ? console.log('|---> Action-mode: INSERT') : '';
				changeDigitalTwinType($("#typeDigitalTwin").val());
			}
			// EDIT MODE ACTION 
			else {	
				logControl ? console.log('|---> Action-mode: UPDATE') : '';
				var type = $("#typeDigital").val();
				$("#typeDigitalTwin").val(type);
				
				AceEditor = ace.edit("aceEditor");
				var logica = digitalTwinCreateJson.logic;
					
				if(logica.charAt(0) === '\"'){
					logica = logica.substr(1, logica.length-2);
				}
				AceEditor.setValue(logica);
			}
			
			
		},
		
		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		
		submitform: function(){
			
			//TODO
			
			$("#digitaltwindevice_create_form").submit();
		},
		
		// DELETE DIGITAL TWIN DEVICE 
		deleteDigitalTwinDevice: function(digitalTwinDeviceId){
			logControl ? console.log(LIB_TITLE + ': deleteDigitalTwinDevice()') : '';	
			deleteDigitalTwinDeviceConfirmation(digitalTwinDeviceId);			
		},
	}
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	DigitalTwinCreateController.load(digitalTwinCreateJson);
	AceEditor = ace.edit("aceEditor");
	DigitalTwinCreateController.init();
});
