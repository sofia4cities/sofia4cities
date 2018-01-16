var MenuController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';	
    var logControl = 1;            
	
	// CONTROLLER PRIVATE FUNCTIONS 	
	
	
	// LOAD MENU INFORMATION FROM USER-ROLE 
	// get SERVER-JSON from header.html -> headerReg.menu and CREATE HTML MENU.
	var consoleMenu = function(){
		
		logControl ? console.log('|---> consoleMenu() -> Creating HTML Console Menu') : '';
		
		// no data no Fun!
		if (!menuReg.menu){ $.alert({title: 'MENU ERROR!',content: 'No Menu Data!'}); return false; }
		
		// get menu data and make HTML menu.
		//var menuJson = JSON.parse(menuReg.menu);
		//console.log('menu: ' + JSON.stringify(menuReg.menu));
		// Append to $('.page-sidebar-menu')
		
	}
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': Función load()') : '';
			return menuReg = Data;
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': Función init()') : '';
			// load menu (role)
			consoleMenu();			
		}		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	MenuController.load(menuJson);
	
	// AUTO INIT CONTROLLER.
	MenuController.init();
});
