var DashboardsCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Dashboard Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	
	function initDatapicker(){
		var dateCreated = $("#datecreated").datepicker('getDate');
	}
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
	// DELETE DASHBOARD
	var deleteDashboardConfirmation = function(dashboardId){
		console.log('deleteDashoardConfirmation() -> formId: '+ dashboardId);
		
		// no Id no fun!
		if ( !dashboardId ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO DASHBOARD-FORM SELECTED!'}); return false; }
		
		logControl ? console.log('deleteDashboardConfirmation() -> formAction: ' + $('.delete-dashboard').attr('action') + ' ID: ' + $('.delete-dashboard').attr('userId')) : '';
		
		// call user Confirm at header.
		HeaderController.showConfirmDialogDashboard('delete_dashboard_form');
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
		deleteDashboard: function(dashboardId){
			logControl ? console.log(LIB_TITLE + ': deleteDashboard()') : '';	
			deleteDashboardConfirmation(dashboardId);			
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	DashboardsCreateController.load();	
		
	// AUTO INIT CONTROLLER.
	DashboardsCreateController.init();
});
