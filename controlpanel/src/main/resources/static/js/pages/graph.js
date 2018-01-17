var GraphController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME  = 'Sofia4Cities Control Panel'
	, LIB_TITLE  = 'Graph Controller'
    , logControl = 1;
	
	// CONTROLLER PRIVATE FUNCTIONS 	
	
	
	
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return graphReg = Data;
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';						
		}		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	GraphController.load(graphJson);
	
	// AUTO INIT CONTROLLER.
	GraphController.init();
});
