var MarketAssetCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.
	var currentFormat = '' // date format depends on currentLanguage.
	var internalFormat = 'yyyy/mm/dd';
	var internalLanguage = 'en';
	var reader = new FileReader();
	    
	reader.onload = function (e) {
        $('#showedImg').attr('src', e.target.result);
    }
	
	// CONTROLLER PRIVATE FUNCTIONS	
    var showGenericErrorDialog= function(dialogTitle, dialogContent){		
		logControl ? console.log('showErrorDialog()...') : '';
		var Close = headerReg.btnCancelar;

		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-bug',
			title: dialogTitle,
			theme: 'dark',
			content: dialogContent,
			draggable: true,
			dragWindowGap: 100,
			backgroundDismiss: true,
			closeIcon: true,
			buttons: {				
				close: {
					text: Close,
					btnClass: 'btn btn-sm btn-default btn-outline',
					action: function (){} //GENERIC CLOSE.		
				}
			}
		});			
	}
    
    var loadType = function(){
    	var url =  marketAssetCreateReg.url + "/fragment";
	    if ($('#marketassetType').val() != '') {
	        url = url + '/' + $('#marketassetType').val();
	    }
	    $("#fragments").load(url);
	    $("#versions").val("");
	    $("#apiDescription").val("");
	}
	
    var loadVersions = function(){
    	var url =  marketAssetCreateReg.url + "/apiversions";
	    if ($('#marketassetType').val() != '') {
	        url = url + '/' + $('#apiId').val();
	    }
	    $("#versions").load(url);
	    $("#apiDescription").val("");
	}
	
    var loadDescription = function(){
		var apiId = $('#apiId').val();
		var version = $("#versions").val()

        $.ajax({
            url: marketAssetCreateReg.url + "/apidescription",
            type: 'POST',
            data: JSON.stringify({"identification": apiId, "version": version}),
            dataType: 'text',
            contentType: 'text/plain',
            mimeType: 'text/plain',
            success: function(data) {
                $("#apiDescription").val(data);
            },
            error: function(data,status,er) {
            }
        });
    }
    
	var validateId = function() {
        var identification = $('#identification').val();

        if ((identification!=null) && (identification.length>0)) {
            $.ajax({
                url: marketAssetCreateReg.url + "/validateId",
                type: 'POST',
                data: JSON.stringify({"identification": identification}),
                dataType: 'text',
                contentType: 'text/plain',
                mimeType: 'text/plain',
                success: function(data) {
                	if (data!=null && data!=""){
                		showGenericErrorDialog('Error', marketAssetCreateReg.marketAssetmanager_identification_error);
                		$('#identification').val("");
                	}
                },
                error: function(data,status,er) {
                	$('#dialog-error').val("ERROR");
                }
            });
        }
    }
    
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
	
	// FORMATDATES: format date to DDBB standard 'yyyy/mm/dd';
	var formatDates = function(dates){
		
		var dateUnformatted = '';
		var dateFormatted	= '';
		
		// no dates no fun!
		if (!dates) { return false;}
		
		// if current language is en , dates are in DDBB format so OK
		if (currentLanguage == 'en') { return true; }
		
		// change all dates to internal format
		logControl ? console.log('formatDates() -> ' + dates + ' with CurrentLanguage: ' + currentLanguage) : '';
		
		
		$(dates).each(function( index, dateInput ) {		  
			if ( $(dateInput).val() ){							
				// ES
				if (currentLanguage === 'es'){
					// change date es to en [ dd/mm/yyyy to yyyy/mm/dd ]
					dateUnformatted = $(dateInput).val();
					dateFormatted = dateUnformatted.split("/")[2] + '/' + dateUnformatted.split("/")[1] + '/' + dateUnformatted.split("/")[0];					
					$(dateInput).val(dateFormatted);
					logControl ? console.log('FormatDate -> ' + $(dateInput).attr('id') + ' current:' + dateUnformatted + ' formatted: ' + $(dateInput).val()) : '';					
				}
				// more languages to come...				
			}		  
		});
		// all formatted then true;
		return true;
	}
	
	// FORM VALIDATION
	var handleValidation = function() {
		logControl ? console.log('handleValidation() -> ') : '';
        // for more info visit the official plugin documentation: 
        // http://docs.jquery.com/Plugins/Validation
		
        var form1 = $('#marketasset_create_form');
        var error1 = $('.alert-danger');
        var success1 = $('.alert-success');
		
		// set current language
		currentLanguage = marketAssetCreateReg.language || LANGUAGE;
		
        form1.validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: ":hidden:not('.selectpicker, .hidden-validation')", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
            messages: {
				datedeleted: { checkdates : marketAssetCreateReg.validation_dates }
			},
			// validation rules
            rules: {
            	identification:		{ minlength: 5, maxlength: 50, required: true },
            	marketassetType:	{ required: true },
            	title:				{ required: true },
            	technologies:		{ required: true },
            	description:		{ required: true },
				datecreated:		{ date: true, required: true }
            },
            invalidHandler: function(event, validator) { //display error alert on form submit              
                success1.hide();
                error1.show();
                App.scrollTo(error1, -200);
            },
            errorPlacement: function(error, element) {
                if 		( element.is(':checkbox'))	{ error.insertAfter(element.closest(".md-checkbox-list, .md-checkbox-inline, .checkbox-list, .checkbox-inline")); }
				else if ( element.is(':radio'))		{ error.insertAfter(element.closest(".md-radio-list, .md-radio-inline, .radio-list,.radio-inline")); }
				else 								{ error.insertAfter(element); }
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
				// date conversion to DDBB format.
                var error = "";
                prepareData();
				if (!formatDates('#datecreated')){
					error = "";
				} 
				if (error == ""){
					form.submit();
				} else { 
					showGenericErrorDialog('ERROR', error);
				}				
            }
        });
    }
		
	// INIT TEMPLATE ELEMENTS
	var initTemplateElements = function(){
		logControl ? console.log('initTemplateElements() -> selectpickers, datepickers, resetForm, today->dateCreated currentLanguage: ' + currentLanguage) : '';
		
		// selectpicker validate fix when handleValidation()
		$('.selectpicker').on('change', function () {
			$(this).valid();
		});
		
		// set current language and formats
		currentLanguage = marketAssetCreateReg.language || LANGUAGE[0];
		currentFormat = (currentLanguage == 'es') ? 'dd/mm/yyyy' : 'mm/dd/yyyy';		
		
		logControl ? console.log('|---> datepickers currentLanguage: ' + currentLanguage) : '';
		
		// init datepickers dateCreated and dateDeleted		
		$("#datecreated").datepicker({dateFormat: currentFormat, showButtonPanel: true,  orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage});
		
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('marketasset_create_form');
		});
		
		// INSERT MODE ACTIONS  (apiCreateReg.actionMode = NULL ) 
		if ( marketAssetCreateReg.actionMode === null){
			logControl ? console.log('action-mode: INSERT') : '';
			
			//set TODAY to dateCreated depends on language
			var f = new Date();         
			today = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',today);
		}
		// EDIT MODE ACTION 
		else {
			var json_desc = JSON.parse($('#json_desc').val());
			
			$('#title').val(json_desc.title);
			$('#technologies').tagsinput('add', json_desc.technologies);
			$('#description').val(json_desc.description);
			
			// set DATE created in EDIT MODE
			logControl ? console.log('action-mode: UPDATE') : '';
			var f = new Date(marketAssetCreateReg.dateCreated);
			regDate = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',regDate);
			
		}
	}
	
    function prepareData(){
    	var json_desc = {"title": $('#title').val() , "technologies": $('#technologies').val(), "description": $('#description').val()};
    	var type=$("#marketassetType").val();
    	
		if (type=='API'){
			json_desc.apiId=$('#apiId').val();
			json_desc.versions=$('#versions').val();
			json_desc.apiDescription=$('#apiDescription').val();
		} else if (type=='DOCUMENT'){
		} else if (type=='APPLICATION'){
			json_desc.installation=$('#installation').val(json_desc.installation);
			json_desc.functionality=$('#functionality').val(json_desc.functionality);
		} else if (type=='WEBPROJECT'){
			json_desc.functionality=$('#functionality').val(json_desc.functionality);
			json_desc.id_endpoint=$('#id_endpoint').val(json_desc.id_endpoint);
		}

		$("#json_desc").val(JSON.stringify(json_desc));
    }
    
    function validateImgSize() {
        if ($('#image').prop('files') && $('#image').prop('files')[0].size>60*1024){
        	showGenericErrorDialog('Error', marketAssetCreateReg.marketAssetmanager_image_error);
        	$('#image').val("");
         } else if ($('#image').prop('files')) {
        	 reader.readAsDataURL($("#image").prop('files')[0]);
         }
    }
    
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		// SHOW ERROR DIALOG
		showErrorDialog: function(dialogTitle, dialogContent) {
			logControl ? console.log(LIB_TITLE + ': showErrorDialog(dialogTitle, dialogContent)') : '';
			showGenericErrorDialog(dialogTitle, dialogContent);
		},

		// VALIDATE IMAGE SIZE
		validateImageSize: function() {
			logControl ? console.log(LIB_TITLE + ': validateImgSize()') : '';
			validateImgSize();
		},
		
		// VALIDATE IDENTIFICATION
		validateIdentification: function() {
			logControl ? console.log(LIB_TITLE + ': validateImgSize()') : '';
			validateId();
		},

		// MARKET ASSET TYPE LOAD
		changeType: function() {
			logControl ? console.log(LIB_TITLE + ': changeType()') : '';
			loadType();
		},
		
		// MARKET ASSET API VERSIONS LOAD
		changeVersions: function() {
			logControl ? console.log(LIB_TITLE + ': changeVersions()') : '';
			loadVersions();
		},
		
		// MARKET ASSET DESCRIPTION LOAD
		changeDescription: function() {
			logControl ? console.log(LIB_TITLE + ': changeDescription()') : '';
			loadDescription();
		},
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return marketAssetCreateReg = Data;
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
		}
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	MarketAssetCreateController.load(marketAssetCreateJson);	
		
	// AUTO INIT CONTROLLER.
	MarketAssetCreateController.init();
});
