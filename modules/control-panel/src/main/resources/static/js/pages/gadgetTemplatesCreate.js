var GadgetsTemplateCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Gadget Template Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
	var navigateUrl = function(url){ window.location.href = url; }
	// DELETE GADGET
	var deleteGadgetTemplateConfirmation = function(gadgetTemplateId){
		console.log('deleteGadgetConfirmation() -> formId: '+ gadgetTemplateId);
		
		// no Id no fun!
		if ( !gadgetTemplateId ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO GATGET TEMPLATE SELECTED!'}); return false; }
		
		logControl ? console.log('deleteGadgetTemplateConfirmation() -> formAction: ' + $('.delete-gadget').attr('action') + ' ID: ' + $('.delete-gadget').attr('userId')) : '';
		
		// call user Confirm at header.
		HeaderController.showConfirmDialogGadgetTemplate('delete_gadget_template_form');	
	}
	
	// INIT CODEMIRROR
	var handleCodeMirror = function () {
		logControl ? console.log('handleCodeMirror() on -> templateCode') : '';	
		
        var myTextArea = document.getElementById('templateCode');
        var myCodeMirror = CodeMirror.fromTextArea(myTextArea, {
        	mode: "code",
            lineNumbers: true,
            foldGutter: true,
            matchBrackets: true,
            styleActiveLine: true,
            theme:"material",         

        })
		myCodeMirror.setSize("100%", 350);
        myCodeMirror.on('change',editor => { var scope = angular.element(document.getElementsByTagName('livehtml')[0]).scope();
		  scope.$$childHead.vm.livecontent=editor.getValue();
          scope.$$childHead.vm.$onChanges([]);          
        });    
    }
	
	var updatePreview = function (){
		var scope = angular.element(document.getElementsByTagName('livehtml')[0]).scope();
		scope.$$childHead.vm.livecontent=$('#templateCode').val();
        scope.$$childHead.vm.$onChanges([]);  
		
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
			handleCodeMirror();
		//	updatePreview();
		},
		
		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		updatePreview: function(){
			updatePreview();
		},
		// DELETE GADGET DATASOURCE 
		deleteGadgetTemplate: function(gadgetId){
			logControl ? console.log(LIB_TITLE + ': deleteGadget()') : '';	
			deleteGadgetTemplateConfirmation(gadgetId);			
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	GadgetsTemplateCreateController.load();	
		
	// AUTO INIT CONTROLLER.
	GadgetsTemplateCreateController.init();
});
