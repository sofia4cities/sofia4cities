var DeviceCreateController = function() {
    
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
	
	
	
	

	
	// FORM VALIDATION
	var handleValidation = function() {
		logControl ? console.log('handleValidation() -> ') : '';
        // for more info visit the official plugin documentation: 
        // http://docs.jquery.com/Plugins/Validation
		
        var form1 = $('#device_create_form');
        var error1 = $('.alert-danger');
        var success1 = $('.alert-success');
		
		// set current language
		currentLanguage = deviceCreateReg.language || LANGUAGE;
		
        form1.validate({
            errorElement: 'span', //default input error message container
            errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: ":hidden:not(.selectpicker)", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
            messages: {
					//datedeleted: { checkdates : deviceCreateReg.validation_dates }
			},
			// validation rules
            rules: {
            	identification:		{ minlength: 5, required: true },
            	description:	{ minlength: 5, required: true },              
              
				
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
            	
				if (valOntologies()){
					 success1.show();
		             error1.hide();
		            // form.submit();
		             insert();
					} else {
					success1.hide();
					$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: deviceCreateJson.ontologyNotSelected}); return false;
					error1.show();										
				}
            }
        });
    }
	
	
	var valOntologies= function (){
		debugger
		return (validateOntologies().length>0);
	}
	
	var insert= function (){
		
		var data = {
				identification : $('#identification').val(),
				description : $('#description').val(),
				clientPlatformOntologies : validateOntologies(),
				metadata : $('#parameter_metaInfo').val()
			}	
	
		
		$.ajax({ 
		type: "POST",
        contentType: "application/json",
        url: "/controlpanel/devices/create",
        data: JSON.stringify(data),
        dataType: 'text',
        cache: false,
        timeout: 600000,
        success:  function(response, e) {
        	
		    	console.log(response);
		    	navigateUrl(response);
        },error : function(e) {
        	
            // $("#createCasesAlert").show();
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
		currentLanguage = deviceCreateReg.language || LANGUAGE[0];
		currentFormat = (currentLanguage == 'es') ? 'dd/mm/yyyy' : 'mm/dd/yyyy';		
		
		logControl ? console.log('|---> datepickers currentLanguage: ' + currentLanguage) : '';
		
	
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('device_create_form');
		});
		
		
		// INSERT MODE ACTIONS  (deviceCreateReg.actionMode = NULL ) 
		if ( deviceCreateReg.actionMode === null){
			logControl ? console.log('action-mode: INSERT') : '';
			//TODO cargar los campos cuando actualicemos
		/*	//set TODAY to dateCreated depends on language
			var f = new Date();         
			today = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',today);
			
			// Set active 
			$('#checkboxactive').trigger('click');*/
		}
		// EDIT MODE ACTION 
		else {
			//TODO cargar los campos cuando actualicemos
			// set DATE created in EDIT MODE
		/*	logControl ? console.log('action-mode: UPDATE') : '';
			var f = new Date(deviceCreateReg.dateCreated);
			regDate = (currentLanguage == 'es') ? ('0' + (f.getDate())).slice(-2) + "/" + ('0' + (f.getMonth()+1)).slice(-2) + "/" + f.getFullYear() : ('0' + (f.getMonth()+1)).slice(-2) + "/" + ('0' + (f.getDate())).slice(-2) + "/" + f.getFullYear();
			$('#datecreated').datepicker('update',regDate);
			
			// set DATE deleted in EDIT MODE if exists
			if ( deviceCreateReg.dateDeleted !== null ) {
				console.log('entra?');
				var d = new Date(deviceCreateReg.dateDeleted);
				regDateDel = (currentLanguage == 'es') ? ('0' + (d.getDate())).slice(-2) + "/" + ('0' + (d.getMonth()+1)).slice(-2) + "/" + d.getFullYear() : ('0' + (d.getMonth()+1)).slice(-2) + "/" + ('0' + (d.getDate())).slice(-2) + "/" + d.getFullYear();
				$('#datedeleted').datepicker('update',regDateDel);
			}			
			
			// if user deleted (active=false, and dateDeleted=date) active=true -> set datadeleted to null.
			$('#checkboxactive').on('click', function(){					
					if (( $('#datedeleted').val() != '' )&&( $(this).is(":checked") )) { $('#datedeleted').datepicker('update',null); $('#datedeleted').prop('disabled',true); }
					console.log('checked in update with datedeleted: ' + $('#datedeleted').val());				
			});
			*/
		}
		
	}	
	
	// DELETE DEVICE
	var deleteDeviceConfirmation = function(id){
		console.log('deleteDeviceConfirmation() -> id: '+ id);
		
		// no Id no fun!
		if ( !id ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO DEVICE-FORM SELECTED!'}); return false; }
		
		
		
		
		// call device Confirm at header.
		HeaderController.showConfirmDialogDevice('delete_device_form');	
	}

	
	
	 
	 var  addMetainfo = function(){
	    	var nombre = document.getElementById("name_metainfo").value;
	    	var valor = document.getElementById("value_metainfo").value;

	    	var p = document.createElement('p');
	    	text = document.createTextNode(nombre + '=' + valor);
	    	
	    	var div = document.createElement('div');
	    	div.className= "metainfo tag label label-info";
	        p.appendChild(text);
	        
	        
	        var span=document.createElement('span');
	        span.className="fa fa-times";  
	        span.onclick = function(){
	            this.parentNode.parentElement.remove()       
	        };
	      
	    	div.appendChild(p);
	    	p.appendChild(span);

	    	$("#id_parameter_metaInfo").append(div);
	    	
	    	if (document.getElementById("parameter_metaInfo").value != ''){
	    		document.getElementById("parameter_metaInfo").value = document.getElementById("parameter_metaInfo").value +'#'+ nombre + '=' + valor;
	    	} else {
	    		document.getElementById("parameter_metaInfo").value = nombre + '=' + valor;
	    	}
	    	
	    
	    	
	    }
	 
	 
	 var addOntologyRow = function (){	
		
		 var ontoSelected =  $( "#onto option:selected" ).text();
		 var levelSelected =  $( "#accessLevel option:selected" ).text();
		 
		console.log(ontoSelected);
		console.log(levelSelected);
		if(ontoSelected===""){
			$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: deviceCreateJson.ontologyNotSelected}); return false;
		}
		 $('#datamodel_properties > tbody').append('<tr data-ontology="'+ontoSelected+'" data-level="'+levelSelected+'"><td>'+ontoSelected+'</td><td >'+levelSelected+'</td><td><button type="button" data-property="" class="btn btn-sm btn-default green-haze btn-outline btn-mountable-remove" onclick="DeviceCreateController.removeOntology(this)" th:text="#{device.ontology.remove}">Remove</button></td></tr>');
		 $(".onto select option:selected").remove();
		 $('.onto').selectpicker('refresh');
	 }
	 
	 var  removeOntology =function(row){
		 var ontoSelected = row.parentElement.parentElement.firstElementChild.innerHTML;
		 $("#onto").append('<option value="'+ontoSelected+'">'+ontoSelected+'</option>');
		 $('.onto').selectpicker('refresh');
		 row.parentElement.parentElement.remove();
	 }
	
	var  validateOntologies = function(){
		var listOntology = [];
		$("#datamodel_properties tbody tr").each(function(tr){
		//	listOntology.push({onto:this.dataset.ontology});
			listOntology.push({id:this.dataset.ontology,level:this.dataset.level});
			
		});
		return listOntology;
	}
  
	
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return deviceCreateReg = Data;
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
		// DELETE DEVICE 
		deleteDevice: function(id){
			logControl ? console.log(LIB_TITLE + ': deleteDevice()') : '';	
			deleteDeviceConfirmation(id);			
		},
		// JSON SCHEMA VALIDATION
		validateOntologies: function(){	
			validateOntologies();			
		},
		addMetainfo:function(){	
			addMetainfo();			
		},
		addOntologyRow:function(){	
			addOntologyRow();			
		},
		removeOntology:function(row){	
			removeOntology(row);			
		},
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	DeviceCreateController.load(deviceCreateJson);	
		
	// AUTO INIT CONTROLLER.
	DeviceCreateController.init();
});
