var DatasourcesCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Ontology Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	
	function initDatapicker(){
		var dateCreated = $("#datecreated").datepicker('getDate');
	}
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
	// DELETE USER
	var deleteDatasourceConfirmation = function(datasourceId){
		console.log('deleteGadgetDatasourceConfirmation() -> formId: '+ datasourceId);
		
		// no Id no fun!
		if ( !datasourceId ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO USER-FORM SELECTED!'}); return false; }
		
		logControl ? console.log('deleteGadgetDatasourceConfirmation() -> formAction: ' + $('.delete-gadgetDatasource').attr('action') + ' ID: ' + $('.delete-gadgetDatasource').attr('userId')) : '';
		
		// call user Confirm at header.
		HeaderController.showConfirmDialogDatasource('delete_datasource_form');	
	}
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return ontologyCreateReg = Data;
		},	
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';
			/*EDITION MODE*/
			/*Hide dimensions*/
			if(!$("[name='id']").val()){
				$("#dimensionsPanel").hide();
			}
			initDatapicker();
		},
		
		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		
		getFieldsFromQueryResult: function (jsonString){
			var fields = [];
			function iterate(obj, stack) {
		        for (var property in obj) {
		            if (obj.hasOwnProperty(property)) {
		                if (typeof obj[property] == "object") {
		                    iterate(obj[property], stack + (stack==""?'':'.') + property);
		                } else {
		                    fields.push(stack + (stack==""?'':'.') + property);
		                }
		            }
		        }
		        
		        return fields;
		    }
	
			return iterate(JSON.parse(jsonString), '');
		},
		
		// DELETE GADGET DATASOURCE 
		deleteGadgetDatasource: function(gadgetDatasourceId){
			logControl ? console.log(LIB_TITLE + ': deleteGadgetDatasource()') : '';	
			deleteDatasourceConfirmation(gadgetDatasourceId);			
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	DatasourcesCreateController.load();	
		
	// AUTO INIT CONTROLLER.
	DatasourcesCreateController.init();
});
