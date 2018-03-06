var ApiCreateController = function() {
    
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
	
	
	var calculateVersion = function() {

        var identification = $('#identification').val();
        var apiType = $('#apiType').val();

        if ((identification!=null) && (identification.length>0)) {
            $.ajax({
                url: 'numVersion',
                type: 'POST',
                data: JSON.stringify({"identification":identification,"apiType":apiType}),
                dataType: 'text',
                contentType: 'text/plain',
                mimeType: 'text/plain',
                success: function(data) {
                    if(data != null && data != "") {
                        $('#numversion').val(data);
                        createOperacionesOntologia ();
                        // VISUAL-UPDATE
                        configurarApi();
                    }
                },
                error: function(data,status,er) {
                    $('#dialog-error').val("ERROR");
                }
            });
        } else {
            configurarApi();
        }
    }

	var configurarApi = function () {
        apiType = $('#apiType').val();
        apiName = $('#identification').val();
        apiVersion = $('#numversion').val();
        apiEndPoint = $('#id_endpoint');
        
        switch (apiType) {
            case 'iot':
            	apiEndPoint.val(endpoint + "/v" + apiVersion + "/" + apiName);
                break;
        }

        // --- configurar panel operaciones
        ontologySelector = $('#ontology');
        ontologyOperations = $('#operacionesOntologia');

        limpiarOperacionesOntologia();
        // empieza con la operaciones limpias
        // borrarOperaciones();

        if (apiType && apiType.startsWith('iot')) {
            // api sobre ontologias
        	ontologySelector.prop('disabled', false);
            createOperacionesOntologia();
        }
    }
	
	function loadOperations () {
        try {
            if ($('#identification').val()!=null){
                for(var i=0; i<operations.length; i+=1){
                    if (isDefaultOp(operations[i].identification)){
                        var id = operations[i].identification;
                        var nameOp = id.substring(id.lastIndexOf("_") + 1);
                        $('#' + nameOp).addClass('op_button_selected').removeClass('op_button');
                        $('#description_' + nameOp).val(operations[i].description);
                        $('#descOperation' + nameOp).show();
                        $('#description_' + nameOp + '_label').text(operations[i].path);
                        $('#div' + nameOp).addClass('op_div_selected');
                    }
                }
            }
        } catch (err) {
            console.log('Fallo cargando operaciones',err);
            $('.capa-loading').hide();
        }
    }
	
	function isDefaultOp(idOp){
		if (idOp.endsWith("_GET") || idOp.endsWith("_GETSQL") || 
			idOp.endsWith("_POST") || idOp.endsWith("_PUT") || 
			idOp.endsWith("_DELETE") || idOp.endsWith("_DELETEID") || 
			idOp.endsWith("_GETOPS")){
			return true;
		} else {
			return false;
		}
	}
	
    function createOperacionesOntologia () {
        $('#description_GET_label').text("/{id}");
        $('#description_GETSQL_label').text("?$filter={query}&$targetdb={targetdb}&$queryType={queryType}&$formatResult={formatResult}");
        $('#description_POST_label').text("/");
        $('#description_PUT_label').text("/");
        $('#description_DELETE_label').text("/");
        $('#description_DELETEID_label').text("/{id}");
        $('#description_GETOPS_label').text("?$query={query}&$queryType={queryType}");
        $('#ontologyOperations input[type="text"]').val('').show();
    }
    
    function limpiarOperacionesOntologia () {
        // desactivar operaciones
        $('#ontologyOperations input.op_button_selected').removeClass('op_button_selected').addClass('op_button');
        // eliminar descripciones y ocultarlas
        $('#ontologyOperations input[type="text"]').val('').css('display',':none');
        // cambia bordes
        $('#ontologyOperations .op_div_selected').removeClass('op_div_selected').addClass('op_div');
        // oculta detalles
        $('#ontologyOperations .op_desc_div').css('display','none');
    }

    var updateCacheTimeout = function () {
        var checkCache= $('#checkboxCache').prop('checked');
        if (checkCache) {
            $('#id_cachetimeout').val("0");
        	$('#id_cachetimeout').prop('disabled', false);
        } else {
        	$('#id_cachetimeout').val("");
        	$('#id_cachetimeout').prop('disabled', true);
        }
    }

    var updateApiLimit = function () {
        var checkCache= $('#checkboxLimit').prop('checked');
        if (checkCache) {
        	$('#id_limit').val("5");
        	$('#id_limit').prop('disabled', false);
        } else {
        	$('#id_limit').val("");
        	$('#id_limit').prop('disabled', true);
        }
    }
	
    function selectOperation(button){
    	if (button.className=='op_button'){
    		button.className='op_button_selected';
    		$('#description_' + button.name).val("");
    		$('#descOperation' + button.name).show();
    		$('#div' + button.name).prop('className', 'op_div_selected');
    	} else if (button.className=='op_button_selected'){
    		button.className='op_button';
    		$('#description_' + button.name).val("");
    		$('#descOperation' + button.name).hide();
    		$('#div' + button.name).prop('className', 'op_div');
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
		
        var form1 = $('#api_create_form');
        var error1 = $('.alert-danger');
        var success1 = $('.alert-success');
		
		// set current language
		currentLanguage = apiCreateReg.language || LANGUAGE;
		
        form1.validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: ":hidden:not(.selectpicker)", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
            messages: {
				datedeleted: { checkdates : apiCreateReg.validation_dates }
			},
			// validation rules
            rules: {
            	identification:		{ minlength: 5, maxlength: 50, required: true },
            	categories:			{ required: true },
            	apiType:			{ required: true },
            	ontology:			{ required: true },
            	id_endpoint:		{ required: true },
            	apiDescripcion:		{ required: true },
            	id_metainf:			{ required: true },
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
                formatData();
				if (!formatDates('#datecreated')){
					error = "";
				} 
				if (error == "" && operations.length==0) {
					error = apiCreateReg.apimanager_noops_error;
				}
				if (error == "" && !validateDescOperations()) {
					error = apiCreateReg.apimanager_ops_description_error;
				}
				if (error == ""){
					form.submit();
				} else { 
					showGenericErrorDialog('ERROR', error);
				}				
            }
        });
    }
		
	function validateDescOperations(){
		var ontology = $("#ontology option:selected").text();
	    if ((ontology!=null) && (ontology.length!=0)){
            if ((($('#GET').attr('class')=='op_button_selected')&&($("#description_GET").val()== ""))
        		|| (($('#GETSQL').attr('class')=='op_button_selected')&&($("#description_GETSQL").val()== ""))
        		|| (($('#POST').attr('class')=='op_button_selected')&&($("#description_POST").val()== ""))
        		|| (($('#PUT').attr('class')=='op_button_selected')&&($("#description_PUT").val()== ""))
        		|| (($('#DELETE').attr('class')=='op_button_selected')&&($("#description_DELETE").val()== ""))
        		|| (($('#DELETEID').attr('class')=='op_button_selected')&&($("#description_DELETEID").val()== ""))
        		|| (($('#GETOPS').attr('class')=='op_button_selected')&&($("#description_GETOPS").val()== ""))){
            		return false;
            }
	    } else if (operations.length=0) {
	    	return false;
	    }
		return true;
	}
	
	
	// INIT TEMPLATE ELEMENTS
	var initTemplateElements = function(){
		logControl ? console.log('initTemplateElements() -> selectpickers, datepickers, resetForm, today->dateCreated currentLanguage: ' + currentLanguage) : '';
		
		// selectpicker validate fix when handleValidation()
		$('.selectpicker').on('change', function () {
			$(this).valid();
		});
		
				
		// set current language and formats
		currentLanguage = apiCreateReg.language || LANGUAGE[0];
		currentFormat = (currentLanguage == 'es') ? 'dd/mm/yyyy' : 'mm/dd/yyyy';		
		
		logControl ? console.log('|---> datepickers currentLanguage: ' + currentLanguage) : '';
		
		// init datepickers dateCreated and dateDeleted		
		$("#datecreated").datepicker({dateFormat: currentFormat, showButtonPanel: true,  orientation: "bottom auto", todayHighlight: true, todayBtn: "linked", clearBtn: true, language: currentLanguage});
		
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('api_create_form');
		});
		
		// INSERT MODE ACTIONS  (apiCreateReg.actionMode = NULL ) 
		if ( apiCreateReg.actionMode === null){
			logControl ? console.log('action-mode: INSERT') : '';
			
			//set TODAY to dateCreated depends on language
			var f = new Date();         
			today = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',today);
		}
		// EDIT MODE ACTION 
		else {
			loadOperations();
			
			$('#id_endpoint').val($('#id_endpoint_hidden').val());
			
			// set DATE created in EDIT MODE
			logControl ? console.log('action-mode: UPDATE') : '';
			var f = new Date(apiCreateReg.dateCreated);
			regDate = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',regDate);	
		}
	}
	
    function replaceOperation(newOp){
        for(var i=0; i<operations.length; i+=1){
            var operation = operations [i];
            if (operation.identification == newOp.identification){
            	operations [i] = newOp;
            }
        }
    }
	
    function existOp(op_name){
        for(var i=0; i<operations.length; i+=1){
            var operation = operations [i];
            if (operation.identification == op_name){
                return true;
            }
        }
        return false;
    }
	
    function formatData(){
    	$('#id_endpoint_hidden').val($('#id_endpoint').val());

        var ontology = $("#ontology option:selected").text();
        if ((ontology!=null) && (ontology.length!=0)){
            var nameApi = $('#identification').val();
            
            var querystringparameter;
            if ($('#GET').attr('class')=='op_button_selected'){
            	var querystringsGET = new Array();
            	var operationGET = {identification: nameApi + "_GET", description: $('#description_GET').val() , operation:"GET", path: $('#description_GET_label').text(), querystrings: querystringsGET};
	            querystringparameter = {name: "id", dataType: "string", headerType: "path", description: ""};
	            operationGET.querystrings.push(querystringparameter);
                if (!existOp(operationGET.identification)){
                	operations.push(operationGET);
                } else {
                    replaceOperation(operationGET);
                }
            }
            if ($('#GETSQL').attr('class')=='op_button_selected'){
            	var querystringsGETSQL = new Array();
            	var operationGETSQL = {identification: nameApi + "_GETSQL", description: $('#description_GETSQL').val() , operation:"GET", path:$('#description_GETSQL_label').text(), querystrings: querystringsGETSQL};
	            querystringparameter = {name: "queryType", dataType: "string", headerType: "query", description: ""};
	            operationGETSQL.querystrings.push(querystringparameter);
	            querystringparameter = {name: "targetdb", dataType: "string", headerType: "query", description: ""};
	            operationGETSQL.querystrings.push(querystringparameter);
	            querystringparameter = {name: "formatResult", dataType: "string", headerType: "query", description: ""};
	            operationGETSQL.querystrings.push(querystringparameter);
	            querystringparameter = {name: "query", dataType: "string", headerType: "query", description: ""};
	            operationGETSQL.querystrings.push(querystringparameter);	            
                if (!existOp(operationGETSQL.identification)){
                	operations.push(operationGETSQL);
                } else {
                    replaceOperation(operationGETSQL);
                }
            }
            if ($('#POST').attr('class')=='op_button_selected'){
            	var querystringsPOST = new Array();
            	var operationPOST = {identification: nameApi + "_POST", description: $('#description_POST').val() , operation:"POST", path:$('#description_POST_label').text(), querystrings: querystringsPOST};
	            querystringparameter = {name: "body", dataType: "string", headerType: "body", description: "", value: "#/definitions/String"};
	            operationPOST.querystrings.push(querystringparameter);
                if (!existOp(operationPOST.identification)){
                	operations.push(operacionPOST);
                } else {
                    replaceOperation(operacionPOST);
                }
            }
            if ($('#PUT').attr('class')=='op_button_selected'){
            	var querystringsPUT = new Array();
            	var operationPUT = {identification: nameApi + "_PUT", description: $('#description_PUT').val() , operation:"PUT", path:$('#description_PUT_label').text(), querystrings: querystringsPUT};
	            querystringparameter = {name: "body", dataType: "string", headerType: "body", description: "", value: "#/definitions/String"};
	            operationPUT.querystrings.push(querystringparameter);
                if (!existOp(operationPUT.identification)){
                	operations.push(operationPUT);
                } else {
                    replaceOperation(operationPUT);
                }
            }
            if ($('#DELETE').attr('class')=='op_button_selected'){
            	var querystringsDELETE = new Array();
            	var operationDELETE = {identification: nameApi + "_DELETE", description: $('#description_DELETE').val() , operation:"DELETE", path:$('#description_DELETE_label').text(), querystrings: querystringsDELETE};
	            querystringparameter = {name: "body", dataType: "string", headerType: "body", description: "", value: "#/definitions/String"};
	            operationDELETE.querystrings.push(querystringparameter);
                if (!existOp(operationDELETE.identification)){
                	operations.push(operationDELETE);
                } else {
                    replaceOperation(operationDELETE);
                }	            
            }
            if ($('#DELETEID').attr('class')=='op_button_selected'){
            	var querystringsDELETEID = new Array();
            	var operationDELETEID = {identification: nameApi + "_DELETEID", description: $('#description_DELETEID').val() , operation:"DELETE", path:$('#description_DELETEID_label').text(), querystrings: querystringsDELETEID};
	            querystringparameter = {name: "id", dataType: "string", headerType: "path", description: ""};
	            operationDELETEID.querystrings.push(querystringparameter);
                if (!existOp(operationDELETEID.identification)){
                	operations.push(operationDELETEID);
                } else {
                    replaceOperation(operationDELETEID);
                }
            }
            if ($('#GETOPS').attr('class')=='op_button_selected'){
            	var querystringsGETOPS = new Array();
            	var operationGETOPS = {identification: nameApi + "_GETOPS", description: $('#description_GETOPS').val() , operation:"GET", path:$('#description_GETOPS_label').text(), querystrings: querystringsGETOPS};
                if (!existOp(operationGETOPS.identification)){
                	operations.push(operationGETOPS);
                } else {
                    replaceOperation(operationGETOPS);
                }
            }
            
            $("#operationsObject").val(JSON.stringify(operations));
            $("#authenticationObject").val(JSON.stringify(authentication));
        }
    }
    
    function validateImgSize() {
        if ($('#image').prop('files') && $('#image').prop('files')[0].size>60*1024){
        	showGenericErrorDialog('Error', apiCreateReg.apimanager_image_error);
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
		
		// CHANGE API CACHE TIMEOUT
		changeCacheTimeout: function() {
			logControl ? console.log(LIB_TITLE + ': changeCacheTimeout()') : '';
			updateCacheTimeout();
		},
		
		
		// CHANGE API LIMIT
		changeApiLimit: function() {
			logControl ? console.log(LIB_TITLE + ': changeApiLimit()') : '';
			updateApiLimit();
		},
		
		// CALCULATE VERSIONS
		calculateNumVersion: function() {
			logControl ? console.log(LIB_TITLE + ': calculateNumVersion()') : '';
			calculateVersion();
		},
		
		// SELECT OPERATIONS
		selectOp: function(button) {
			logControl ? console.log(LIB_TITLE + ': selectOp()') : '';
			selectOperation(button);
		},
		
		// SELECT OPERATIONS
		existOperation: function(name) {
			logControl ? console.log(LIB_TITLE + ': existOperation(name)') : '';
			return existOp(name);
		},
		
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return apiCreateReg = Data;
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
	ApiCreateController.load(apiCreateJson);	
		
	// AUTO INIT CONTROLLER.
	ApiCreateController.init();
});
