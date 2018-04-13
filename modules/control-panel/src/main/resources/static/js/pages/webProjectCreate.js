var WebProjectCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Web Project Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	
	// CONTROLLER PRIVATE FUNCTIONS	

    var uploadZip = function (){
    	$('#updateBtn').attr('disabled','disabled');
    	$('#createBtn').attr('disabled','disabled');    	
    	$('#deleteBtn').attr('disabled','disabled');    	
    	$('#resetBtn').attr('disabled','disabled');    	
    	$.ajax({
            type: 'post',
            url: '/controlpanel/webprojects/uploadZip',
            contentType: false,
            processData: false,
            data: new FormData($('#upload_zip')[0]),
            success: function () {
            	$('#updateBtn').removeAttr('disabled');
            	$('#createBtn').removeAttr('disabled'); 
            	$('#deleteBtn').removeAttr('disabled');    	
            	$('#resetBtn').removeAttr('disabled');   
            },
            error: function(){
            }
        });
    }
    
	// REDIRECT URL
	var navigateUrl = function(url){
		window.location.href = url; 
	}
	
    // CONTROLLER PUBLIC FUNCTIONS 
	return{
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';
			
		},

		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		
		// uploadZip
		uploadZip: function(url){
			logControl ? console.log(LIB_TITLE + ': uploadZip()') : '';	
			uploadZip(); 
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	WebProjectCreateController.load(webProjectCreateJson);	
		
	// AUTO INIT CONTROLLER.
	WebProjectCreateController.init();
});