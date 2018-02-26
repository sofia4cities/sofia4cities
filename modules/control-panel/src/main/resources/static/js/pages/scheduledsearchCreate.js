  var ontologyExist=true;
  var clientExist=true;
  

function submitForm()
{
	if($('#_checkboxnew').val($('#checkboxnew').is(':checked'))){
		 existOntology();
		 existClient();
		 if($('input[name=ontologyId]').val()!="")
			{
				if(ontologyExist==false && clientExist==false) $('#scheduledsearch_create_form').submit();
				else
				{
					if(ontologyExist==true) 
					{
						hideErrors();
						$('.alert-generic').show();
						$('.alert-exists-text').html("Ontology Exists");
					}
					else if(clientExist==true)
					{ 
						hideErrors();
						$('.alert-generic').show();
						$('.alert-exists-text').html("Platform Client Exists");
					}
				}
			}else{
				$('#scheduledsearch_create_form').submit();
			}

	}
 
	
}
function existOntology(){
	var identification = $('input[name=ontologyId]').val();
  return $.ajax({ 
		url: "/controlpanel/twitter/scheduledsearch/existontology",
		type: 'POST',
		data:identification,
    async: false,
		dataType: 'text', 
		contentType: 'text/plain',
		mimeType: 'text/plain',
		success: function(exists) { 
		  if(exists=="false") ontologyExist=false;
      else ontologyExist=true;
		}
	});
}


function existClient(callback){
	var identification = $('input[name=clientPlatformId]').val();
	return $.ajax({ 
		url: "/controlpanel/twitter/scheduledsearch/existclient", 
		type: 'POST',
		data: identification,
    async: false,
		dataType: 'text', 
		contentType: 'text/plain',
		mimeType: 'text/plain',
		success: function(exists) { 
      if(exists=="false") clientExist=false;
      else clientExist=true;      
		}
	}); 
}



function newOntology(){
	if($('#row-new').is(':visible'))
	{
		$('#row-new').hide();
		$('#row-not-new').show();
	}else{
		$('#row-new').show();
		$('#row-not-new').hide();
	}
	$('#_checkboxnew').val($('#checkboxnew').is(':checked'));

}

function getClients(){
	var ontologyId = $("#ontologies").find(":selected").val();
	$("#clientplatforms").empty();
	$("#tokens").empty();

	if(ontologyId!=""){
		$.ajax({
			url:"/controlpanel/twitter/scheduledsearch/getclients",
			type: 'POST',
			data: ontologyId,
			dataType:'json',
			contentType: 'application/json',
			mimeType: 'application/json',
			success: function(data) {
				$("#clientplatforms").show();
				if (Object.prototype.toString.apply(data) === '[object Array]') {
					if(data.length>0){
						for (i = 0; i < data.length; i += 1) {
							var o = new Option(data[i]);
							$("#clientplatforms").append(o);
						}
						getTokens();
					}else{
						$('.alert-generic').show();
						$('.alert-exists-text').html("No clients found for this ontology");
					}

				}
			},
			error:function(data, status, er) { 
				$('.alert-generic').show();
				$('.alert-exists-text').html("Error: "+status);
			}
		}); 
	}

}

function getTokens(){
	var clientPlatformId = $("#clientplatforms").find(":selected").text();
	$("#tokens").empty();
	$.ajax({
		url:"/controlpanel/twitter/scheduledsearch/gettokens",
		type: 'POST',
		data: clientPlatformId,
		dataType:'json',
		contentType: 'application/json',
		mimeType: 'application/json',
		success: function(data) {
			$("#tokens").show();
			if (Object.prototype.toString.apply(data) === '[object Array]') {
				if(data.length>0){
					for (i = 0; i < data.length; i += 1) {
						var o = new Option(data[i]);
						$("#tokens").append(o);
					}
				}else{
					$('.alert-generic').show();
					$('.alert-exists-text').html("No tokens found for this client");
				}

			}
		},
		error:function(data, status, er) { 
			$('.alert-generic').show();
			$('.alert-exists-text').html("Error: "+status);
		}
	}); 
}
function hideErrors()
{
	$('.alert').hide();

}
var ScheduledSearchController= function()
{

	// DEFAULT PARAMETERS, VAR, CONSTS. 
	var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';  
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

	// CHECK DATES AND LET THE FORM SUBMMIT
	var checkCreate = function(){
		logControl ? console.log('checkCreate() -> ') : '';

		var dateFrom = $("#dateFrom").datepicker('getDate');
		var dateTo = $("#dateTo").datepicker('getDate');

		var diff = dateTo - dateFrom;
		var days = diff / 1000 / 60 / 60 / 24;

		logControl ?  console.log('created: ' + dateFrom + '  deleted: ' + dateTo): '';   

		if (dateTo != ""){
			if (dateFrom > dateTo){
				$.confirm({icon: 'fa fa-warning', title: 'CONFIRM:', theme: 'dark',
					content: scheduledSearchCreateReg.validation_dates,
					draggable: true,
					dragWindowGap: 100,
					backgroundDismiss: true,
					closeIcon: true,
					buttons: {        
						close: { text: scheduledSearchCreateReg.Close, btnClass: 'btn btn-sm btn-default btn-outline', action: function (){} //GENERIC CLOSE.    
						}
					}
				});
				$("#dateTo").datepicker('update','');
			}                
		}
	} 


	// FORMATDATES: format date to DDBB standard 'yyyy/mm/dd';
	var formatDates = function(dates){

		var dateUnformatted = '';
		var dateFormatted = '';

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

		var form1 = $('#scheduledsearch_create_form');
		var error1 = $('.alert-danger');
		var success1 = $('.alert-success');
		var errorGeneric= $('.alert-generic');

		// set current language
		currentLanguage = scheduledSearchCreateReg.language || LANGUAGE;

		form1.validate({
			errorElement: 'span', //default input error message container
			errorClass: 'help-block help-block-error', // default input error message class
			focusInvalid: false, // do not focus the last invalid input
			ignore: ":hidden:not(.selectpicker)", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
			messages: {
				dateTo: { checkdates : scheduledSearchCreateReg.validation_dates }
			},
			// validation rules
			rules: {
				userId:   { minlength: 5, required: true },
				fullName: { minlength: 5, required: true },
				email:    { required: true, email: true },
				password: { required: true, minlength: 7, maxlength: 20 },
				roles:    { required: true },
				dateFrom:{ date: true, required: true },
				dateTo:{ date: true }
			},
			invalidHandler: function(event, validator) { //display error alert on form submit              
				success1.hide();
				error1.show();
				App.scrollTo(error1, -200);
			},
			errorPlacement: function(error, element) {
				if    ( element.is(':checkbox'))  { error.insertAfter(element.closest(".md-checkbox-list, .md-checkbox-inline, .checkbox-list, .checkbox-inline")); }
				else if ( element.is(':radio'))   { error.insertAfter(element.closest(".md-radio-list, .md-radio-inline, .radio-list,.radio-inline")); }
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
				// date conversion to DDBB format.
				if ( formatDates('#dateFrom,#dateTo') ) {           
					form.submit();
				} 
				else { 
					success1.hide();
					error1.show();
					App.scrollTo(error1, -200);
				}       
			}
		});
	}


	// INIT TEMPLATE ELEMENTS
	var initTemplateElements = function(){
		logControl ? console.log('initTemplateElements() -> selectpickers, datepickers, resetForm, today->dateFrom currentLanguage: ' + currentLanguage) : '';

		// selectpicker validate fix when handleValidation()
		$('.selectpicker').on('change', function () {
			$(this).valid();
		});

		// set current language and formats
		currentLanguage = scheduledSearchCreateReg.language || LANGUAGE[0];
		currentFormat = (currentLanguage == 'es') ? 'dd/mm/yyyy, h:mm:ss' : 'mm/dd/yyyy ,h:mm:ss';    

		logControl ? console.log('|---> datepickers currentLanguage: ' + currentLanguage) : '';

		// init datepickers dateFrom and dateTo   
		$("#dateFrom").datepicker({dateFormat: currentFormat, showButtonPanel: true, sideBySide: true, orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage});
		var dd = $("#dateTo").datepicker({dateFormat: currentFormat, showButtonPanel: true,  orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage});

		// setting on changeDate to checkDates()
		dd.on('changeDate', function(e){
			//gets the full date formated
			selectedDate = dd.data('datepicker').getFormattedDate(currentFormat);       
			checkCreate();
		});

		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('scheduledsearch_create_form');
		});

		//set TODAY to dateFrom depends on language INSERT-MODE ONLY   
		if ( scheduledSearchCreateReg.actionMode === null){
			logControl ? console.log('action-mode: INSERT') : '';
			var f = new Date();         
			today = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#dateFrom').datepicker('update',today);
		}
		else {
			// set DATE created in EDIT MODE
			logControl ? console.log('action-mode: UPDATE') : '';
			var f = new Date(scheduledSearchCreateReg.dateFrom);
			regDate = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#dateFrom').datepicker('update',regDate);

			// set DATE deleted in EDIT MODE if exists
			if ( scheduledSearchCreateReg.dateTo !== null ) {
				console.log('entra?');
				var d = new Date(scheduledSearchCreateReg.dateTo);
				regDateDel = (currentLanguage == 'es') ? ('0' + (d.getDate())).slice(-2) + "/" + ('0' + (d.getMonth()+1)).slice(-2) + "/" + d.getFullYear() : ('0' + (d.getMonth()+1)).slice(-2) + "/" + ('0' + (d.getDate())).slice(-2) + "/" + d.getFullYear();
				$('#dateTo').datepicker('update',regDateDel);
			}     



		}

	} 
  // DELETE twitterListening
  var deleteTwitterListeningConfirmation = function(twitterListeningId){
    console.log('deletetwitterListeningConfirmation() -> formId: '+ twitterListeningId);
    
    
    // set action and twitterListeningId to the form
    $('.delete-twitterListening').attr('id',twitterListeningId);
    $('.delete-twitterListening').attr('action','/controlpanel/twitter/scheduledsearch/' + twitterListeningId);
    console.log('deletetwitterListeningConfirmation() -> formAction: ' + $('.delete-twitterListening').attr('action') + ' ID: ' + $('.delete-twitterListening').attr('userId'));
    
    // call twitterListening Confirm at header.
    HeaderController.showTwitterListeningConfirmDialog(twitterListeningId); 
  }



	// CONTROLLER PUBLIC FUNCTIONS 
	return{   
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return scheduledSearchCreateReg = Data;
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
    // DELETE CONFIG
    deletetwitterListening: function(twitterListeningId){
      logControl ? console.log(LIB_TITLE + ': deletetwitterListening()') : ''; 
      deleteTwitterListeningConfirmation(twitterListeningId);     
    }


	};
}();
//AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {

	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	ScheduledSearchController.load(ScheduledSearchCreateJson);  

	// AUTO INIT CONTROLLER.
	ScheduledSearchController.init();
});