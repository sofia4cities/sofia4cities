var OntologyCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Ontology Create/Update/Remove Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.
	var currentFormat = '' // date format depends on currentLanguage.
	var internalFormat = 'yyyy/mm/dd';
	var internalLanguage = 'en';
	
	// CONTROLLER PRIVATE FUNCTIONS	

	
	// REDIRECT URL
	var navigateUrl = function(url){ window.location.href = url; }
	
		
	// CLEAN FIELDS FORM
	var cleanFields = function (formId) {
		logControl ? console.log('cleanFields() -> ') : '';
		
		//CLEAR OUT THE VALIDATION ERRORS
		$('#'+formId).validate().resetForm(); 
		$('#'+formId).find('input:text, input:password, input:file, select, textarea').each(function(){
			// CLEAN ALL EXCEPTS cssClass "no-remote" persistent fields
			if(!$(this).hasClass("no-remove")){$(this).val('');}
		});
		
		//CLEANING SELECTs
		$(".selectpicker").each(function(){
			$(this).val( '' );
			$(this).selectpicker('deselectAll').selectpicker('refresh');
		});
		
		// CLEAN ALERT MSG
		$('.alert-danger').hide();
	}
	
			
	// FORM VALIDATION
	var handleValidation = function() {
		logControl ? console.log('handleValidation() -> ') : '';
        // for more info visit the official plugin documentation: 
        // http://docs.jquery.com/Plugins/Validation
		
        var form1 = $('#ontology_create_form');
        var error1 = $('.alert-danger');
        var success1 = $('.alert-success');
		
		// set current language
		currentLanguage = ontologyCreateReg.language || LANGUAGE;
		
        form1.validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: ":hidden:not(.selectpicker)", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
            messages: {					
			},
			// validation rules
            rules: {
				ontologyId:		{ minlength: 5, required: true },
                identification:	{ minlength: 5, required: true },
				metainf:		{ minlength: 5, required: true },
				description:	{ required: true }                
            },
            invalidHandler: function(event, validator) { //display error alert on form submit              
                success1.hide();
                error1.show();
                App.scrollTo(error1, -200);
            },
            errorPlacement: function(error, element) {
                if 		( element.is(':checkbox'))	{ error.insertAfter(element.closest(".md-checkbox-list, .md-checkbox-inline, .checkbox-list, .checkbox-inline")); }
				else if ( element.is(':radio'))		{ error.insertAfter(element.closest(".md-radio-list, .md-radio-inline, .radio-list,.radio-inline")); }
				else { error.insertAfter(element); }
            },
            highlight: function(element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); 
            },
            unhighlight: function(element) { // revert the change done by hightlight
                $(element).closest('.form-group').removeClass('has-error');
            },
            success: function(label) {
                label.closest('.form-group').removeClass('has-error');
            },
			// ALL OK, THEN SUBMIT.
            submitHandler: function(form) {
                success1.show();
                error1.hide();
				form.submit();
			}
        });
    }
	
	
	// INIT TEMPLATE ELEMENTS
	var initTemplateElements = function(){
		logControl ? console.log('initTemplateElements() -> selectpickers, datepickers, resetForm,  currentLanguage: ' + currentLanguage) : '';
		
		// selectpicker validate fix when handleValidation()
		$('.selectpicker').on('change', function () {
			$(this).valid();
		});
		
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('ontology_create_form');
		});		
		
		// INSERT MODE ACTIONS  (ontologyCreateReg.actionMode = NULL ) 
		if ( ontologyCreateReg.actionMode === null){
			logControl ? console.log('|---> Action-mode: INSERT') : '';
			
			// Set active 
			$('#active').trigger('click');
			
			// Set Public 
			$('#public').trigger('click');
		}
		// EDIT MODE ACTION 
		else {	
			logControl ? console.log('|---> Action-mode: UPDATE') : '';
			
		}
		
	}	
	
	// DELETE ONTOLOGY
	var deleteOntologyConfirmation = function(ontologyId){
		console.log('deleteOntologyConfirmation() -> formId: '+ ontologyId);
		
		// no Id no fun!
		if ( !ontologyId ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO ONTOLOGY-FORM SELECTED!'}); return false; }
		
		// No dateDeleted no fun!
		if ( !$('#datedeleted').val() ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: ontologyCreateReg.validation_dates}); return false; }
		
		logControl ? console.log('deleteOntologyConfirmation() -> formAction: ' + $('.delete-ontology').attr('action') + ' ID: ' + $('.delete-ontology').attr('ontologyId')) : '';
		
		// call ontology Confirm at header. TO-DO: ver que show toca
		HeaderController.showConfirmDialogUsuario('delete_ontology_form');	
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
			handleValidation();
			initTemplateElements();		
			
		},
		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		// DELETE ONTOLOGY 
		deleteOntology: function(ontologyId){
			logControl ? console.log(LIB_TITLE + ': deleteOntology()') : '';	
			deleteOntologyConfirmation(ontologyId);			
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	OntologyCreateController.load(ontologyCreateJson);	
		
	// AUTO INIT CONTROLLER.
	OntologyCreateController.init();
});
