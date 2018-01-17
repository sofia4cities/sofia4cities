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
		
		var menu_HTML , submenu_HTML , markUp_HTML = '';
		
		// no data no Fun!
		if (!menuReg){ $.alert({title: 'MENU ERROR!',content: 'No Menu Data!'}); return false; }
		
		// get menu data and make HTML menu.
		var menuJson = menuReg;
		logControl ? console.log('menu: ' + menuJson.menu + ' NoSession Path: ' + menuJson.noSession + ' Rol: ' + menuJson.rol + ' Navigation Objects: ' + menuJson.navigation.length + ' Language: ES') : '';
		
		// MENU NAV-ITEMS ARRAY
		var navItemsArr = menuJson.navigation;
		// NAV-ITEM LOOP
		navItemsArr.map(function(item, index){
			if ( hasSubmenus(item) ){ submenus = '(sub-menus)'} else { submenus = '(NO sub-menus)'; }
			console.log('navItem-' + index + 'Item: ' + item.title.ES + ';  Submenus: ' + item.submenu.length + ' ' + submenus );
			
			// SUB-NAV-ITEM LOOP
			if ( hasSubmenus(item) ){ 
				item.submenu.map(function(subitem, subindex){					
					console.log('|---> sub navItem-'+ subindex + '; SubItem: ' + subitem.title.ES + '.');					
				});			
			}
		});		
	}
	
	// CHECK IF A NAV-ITEM HAD SUBMENU ITEMS
	var hasSubmenus = function(item){ var checkSubmenus = item.submenu.length > 0 ? true : false; return checkSubmenus;  }
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return menuReg = Data;
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';
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
