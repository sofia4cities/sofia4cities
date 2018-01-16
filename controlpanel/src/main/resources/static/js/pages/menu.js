var MenuController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';	
    var logControl = 1;
    var alertContainer = '.alert-zone';         
	
	// PRIVATE FUNCTIONS 	
	
	
	// LOAD MENU INFORMATION FROM USER-ROLE 
	// get JSON from header.html -> headerReg.menu 
	var consoleMenu = function(){
		
		logControl ? console.log('|---> consoleMenu() -> Creating HTML Console Menu') : '';
		
		// no data no Fun!
		if (!menuReg.menu){ $.alert({title: 'MENU ERROR!',content: 'No Menu Data!'}); return false; }
		
		// get menu data and make HTML 
		//var menuJson = JSON.parse(menuReg.menu);
		//console.log('menu: ' + JSON.stringify(menuReg.menu));
		// debemos agregar el conjunto con un append al $('.page-sidebar-menu')
		
	}
	
	// ZONA DE FUNCIONES PUBLICAS ENTRE ELLAS INIT.
	return{
		
		// LOAD() CARGA UN JSON DE PARAMETROS DE LA PLANTILLA A LA LIB.
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': Función load()') : '';
			return menuReg = Data;
		},
		
		// INIT() INICIALIZACION DE LA LIB , EJECUCIÓN DE TODAS LAS FUNCIONES QUE SE DEBEN LANZAR AL ENTRAR EN LA PÁGINA.
		init: function(){
			logControl ? console.log(LIB_TITLE + ': Función init()') : '';
			// load menu (role)
			consoleMenu();			
		}		
	};
}();

// INICIALIZACIÓN AUTOMÁTICA DEL CONTROLADOR
jQuery(document).ready(function() {
	
	// LLAMAMOS AL JSON DE ETIQUETAS DE LENGUAJE y DATOS
	MenuController.load(menuJson);
	
	// LLAMAMOS AL INIT
	MenuController.init();
});
