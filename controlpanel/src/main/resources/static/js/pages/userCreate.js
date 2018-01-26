var UserCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.
	
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
	}
	
	// CHECK DATES AND LET THE FORM SUBMMIT
	var checkCreate = function(){
		logControl ? console.log('checkCreate() -> ') : '';
        
		var dateCreated = $("#datecreated").val();
        var dateDeleted = $("#dateleted").val();
        var created = new Date(dateCreated);
        var deleted = new Date(dateDeleted);  
		logControl ?  console.log('created: ' + dateCreated + '  deleted: ' + dateDeleted): '';
        if (dateDeleted != ""){
            if (created > deleted){
                $.confirm({icon: 'fa fa-warning', title: 'CONFIRM:', theme: 'dark',
					content: userCreateReg.validation_dates,
					draggable: true,
					dragWindowGap: 100,
					backgroundDismiss: true,
					closeIcon: true,
					buttons: {				
						close: { text: Close, btnClass: 'btn btn-sm btn-default btn-outline', action: function (){} //GENERIC CLOSE.		
						}
					}
				});
                return false;
            }
			else{ return true; }           
        }
    }
	
	// FORM VALIDATION
	var handleValidation = function() {
		logControl ? console.log('handleValidation() -> ') : '';
        // for more info visit the official plugin documentation: 
        // http://docs.jquery.com/Plugins/Validation
		
        var form1 = $('#user_create_form');
        var error1 = $('.alert-danger');
        var success1 = $('.alert-success');
		
		// set current language
		currentLanguage = userCreateReg.language || LANGUAGE;
		
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
				userId:		{ minlength: 5, required: true },
                fullName:	{ minlength: 5, required: true },
                email:		{ required: true, email: true },
                password:	{ required: true, minlength: 7, maxlength: 20 },
                roles:		{ required: true },
				datecreated:{ date: true },
				datedeleted:{ date: true }
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
		logControl ? console.log('initTemplateElements() -> selectpickers, datepickers, resetForm, today->dateCreated') : '';
		
		// selectpicker validate fix when handleValidation()
		$('.selectpicker').on('change', function () {
			$(this).valid();
		});
		
		// set current language
		currentLanguage = userCreateReg.language || LANGUAGE;
		
		// init datepickers dateCreated and dateDeleted
		$("#datecreated").datepicker({dateFormat: "mm/dd/yy", showButtonPanel: true,  orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage});
        $("#datedeleted").datepicker({dateFormat: "mm/dd/yy", showButtonPanel: true,  orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage,
			onClose: function(e) {
					var ev = window.event;
					if (ev.srcElement.innerHTML == 'Clear')
					this.value = "";    
			},
			closeText: 'Clear',
			buttonText: ''   
        });
		
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('user_create_form');
		});
		
		// set TODAY to dateCreated
		var f = new Date();         
        $('#datecreated').val(('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear());
		
	}

	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return userCreateReg = Data;
		},	
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';			
			handleValidation();
			initTemplateElements();
		},
		checkDates: function(){
			logControl ? console.log(LIB_TITLE + ': checkDates()') : '';	
			checkCreate();
			
		}
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	UserCreateController.load(userCreateJson);	
		
	// AUTO INIT CONTROLLER.
	UserCreateController.init();
});
