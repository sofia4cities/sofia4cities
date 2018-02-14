var OntologyCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Ontology Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	var validTypes = ["string","object","number","date","timestamp","array","binary"]; // Valid property types
	var schema = ''; // current schema json string var
	var mountableModel = $('#datamodel_properties').find('tr.mountable-model')[0].outerHTML; // save html-model for when select new datamodel, is remove current and create a new one.
	
	
	
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
	
	// AUX. DATAMODEL PROPERTIES OBJECT JSON
	var createJsonProperties = function (jsonData){
		logControl ? console.log('|---  createJsonProperties()') : '';
		
		var jsonFormatted 	= [];
		var properties 		= [];
		var required 		= [];
		var propObj			= {};
		var propRequired	= '';
		
		// Required
		if ( jsonData.hasOwnProperty('datos') ){ required = jsonData.datos.required; } else { required = jsonData.required;  }
				
		// Properties
		if ( jsonData.hasOwnProperty('datos') ){ properties = jsonData.datos.properties; } else { properties = jsonData.properties;  }
				
		// KEY and VALUE (value or object, or array...)
		$.each( properties, function (key, object){			
			if (object){
				console.log('|--- Key: '+ key );
				$.each(object, function (propKey, propValue){					
					if ( propKey == 'type'){ 						
						// check required						
						propRequired = $.inArray( key, required ) > -1 ? propRequired = 'required' : propRequired = '';
						
						// add property to properties
						propObj = {"property": key, "type": propValue, "required": propRequired, "description": ''};
						jsonFormatted.push(propObj);						
					}
				});
			}
		});		
		return jsonFormatted;
	}
	
	
	// AUX. getProperties return properties array
	var getProperties = function(json){	
		logControl ? console.log('   |---  getProperties()') : '';
		var arrProperties = [];
			
		// KEYs
		$.each( json, function (key, object){						
			$.each(object, function (key, value){
				if (value){ if ( key == 'property') { arrProperties.push(value); } } 
			});			
		});			
		logControl ? console.log('      |----- getProperties: ' + JSON.stringify(arrProperties)) : '';
		return arrProperties;	
	}
	
	
	// AUX. UPDATE SCHEMA FROM ADDITIONAL PROPERTIES, SCHEMA IS BASE CURRENT SCHEMA LOADED
	var updateSchemaProperties = function(){
		logControl ? console.log('updateSchemaProperties() -> ') : '';
		
		// properties, types and required arrays
		var updateProperties = $("input[name='property\\[\\]']").map(function(){ if ($(this).val() !== ''){ return $(this).val(); }}).get();				
		var updateTypes = $("input[name='type\\[\\]']").map(function(){return $(this).val();}).get();
		var updateRequired = $("input[name='required\\[\\]']").map(function(){return $(this).val();}).get();
		
		
		var schemaObj = {};
		
		logControl ? console.log('|--- CURRENT: ' + updateProperties + ' types: ' + updateTypes + ' required: ' + updateRequired): '';
		
		checkUnique = updateProperties.unique();
		if (updateProperties.length !== checkUnique.length)  { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'HAY DUPLICADOS, REVISE!'}); return false; } 
				
		// get current schema 
		if (typeof schema == 'string'){		
			schemaObj = JSON.parse(schema);				
			
		}else if (typeof schema == 'object') { schemaObj = schema; } else { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'NO TEMPLATE SCHEMA!'}); return false; }
		
		// compare properties added with properties on current schema
		baseJson = createJsonProperties(schemaObj);
				
		// schema string -> Object --> update --> toString --> to editor.
		baseArrProperties	= getProperties(baseJson);
		
				
		// COMPARE BASE WITH CURRENT
		var toUpdateProperties = [];
		var toUpdateTypes = [];
		var toUpdateRequired = [];
		var i = 0;		
		
		// DIFFERENCE BETWEEN BASE vs CURRENT
		jQuery.grep(updateProperties, function(el) {
			if (jQuery.inArray(el, baseArrProperties) == -1) toUpdateProperties.push(el);
				i++;
		});	
		
		logControl ? console.log(" |-------------  the difference are " + toUpdateProperties + ' elements: ' + toUpdateProperties.length): '';
		
		
		// UPDATE SCHEMA
		var toUpdateSchema = [];
		if ( toUpdateProperties.length ){			
			// get properties to update, use her index to access to type and required, mount object and update the schema.			
			$.each(toUpdateProperties, function( index, value ) {
				propIndex = updateProperties.indexOf(value);
				logControl ? console.log('index: ' + propIndex + ' | property: ' + updateProperties[propIndex] + ' type: ' + updateTypes[propIndex] + ' required: ' + updateRequired[propIndex]) : '';
					// update property on Schema /current are stored in schema var.  (property,type,required)
					updateProperty(updateProperties[propIndex], updateTypes[propIndex], updateRequired[propIndex] );					
			});			
		}
	}
	
	
	// AUX. UPDATE PROPERTY IN SCHEMA FOR EACH NEW PROPERTY ADDED
	var updateProperty = function(prop, type, req){
		logControl ? console.log('|---   updateProperty() -> ') : '';
		
		var properties = [];
		var requires = [];
		
		
		if 		(typeof schema == 'string'){ data = JSON.parse(schema); }
		else if (typeof schema == 'object'){ data = schema; } else { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'NO TEMPLATE SCHEMA!'}); return false; }		
			
		// SCHEMA MODEL ( PROPERTIES / DATOS) 
		if ( data.hasOwnProperty('datos') ){ properties = data.datos.properties; requires = data.datos.required; } else { properties = data.properties;  requires = data.required }
	
		// ADD PROPERTY+TYPE
		if (type != 'timestamp'){
			properties[prop] = { "type": type};
		} 
		else {			
			properties[prop] = {"type": "object", "required": ["$date"],"properties": {"$date": {"type": "string","format": "date-time"}}}
		
		}
		
		// ADD REQUIRED
		if (req == 'required') {  requires.push(prop); } 
		
		console.log('added: ' + prop +' with type: ' + type + ' required: ' + req);
		console.log('JSON: ' + JSON.stringify(data));
		
		// ADD additionalProperties, because we are adding properties.
		if (!data.hasOwnProperty('additionalProperties')){
			data["additionalProperties"] = true;			
		}	
		
		// ADD INFO TO SCHEMA EDITOR
		editor.setMode("text");
        editor.setText('');
		editor.setText(JSON.stringify(data));
		editor.setMode("tree");	

		// UDATING SCHEMA STRING
		schema = JSON.stringify(data);
		
		// UPDATING FORM FIELDS
		$('#jsonschema').val(schema);
		
	}
	
	
	// REDIRECT URL
	var navigateUrl = function(url){
		window.location.href = url; 
	}
	
	
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
		logControl ? console.log('initTemplateElements() ->  resetForm,  currentLanguage: ' + currentLanguage) : '';
		
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
			
			// take schema from ontology and load it
			schema = ontologyCreateReg.schemaEditMode;
			
			
			// overwrite datamodel schema with loaded ontology schema generated with this datamodel  template.
			var theSelectedModel = $("h3[data-model='"+ ontologyCreateReg.dataModelEditMode +"']");
			var theSelectedModelType = theSelectedModel.closest('div .panel-collapse').parent().find("a").trigger('click');			
			theSelectedModel.attr('data-schema',schema).trigger('click');
			
			
			
			
		}
		
	}	
	
	
	// DELETE ONTOLOGY
	var deleteOntologyConfirmation = function(ontologyId){
		console.log('deleteOntologyConfirmation() -> formId: '+ ontologyId);
		
		// no Id no fun!
		if ( !ontologyId ) {$.alert({title: 'ERROR!', type: 'red' , theme: 'dark', content: 'NO ONTOLOGY-FORM SELECTED!'}); return false; }
		
		logControl ? console.log('deleteOntologyConfirmation() -> formAction: ' + $('.delete-ontology').attr('action') + ' ID: ' + $('#delete-ontologyId').attr('ontologyId')) : '';
		
		// call ontology Confirm at header. 
		HeaderController.showConfirmDialogOntologia('delete_ontology_form');	
	}

		
	// CREATE EDITOR FOR JSON SCHEMA 
	var createEditor = function(){
		
		logControl ? console.log('|--->   createEditor()') : '';
		var container = document.getElementById('jsoneditor');	
		var options = {
			mode: 'code',
			theme: 'bootstrap3',
			required_by_default: true,
			modes: ['code', 'text', 'tree', 'view'], // allowed modes
			error: function (err) {
				$.alert({title: 'ERROR!', theme: 'dark', style: 'red', content: err.toString()});
				return false;
			}
		};
		
		editor = new jsoneditor.JSONEditor(container, options, "");		
		
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
			createEditor();
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
		},
		
		// REMOVE PROPERTYS (ONLY ADDITIONAL NO BASE)
		removeProperty: function(obj){
			logControl ? console.log(LIB_TITLE + ': removeProperty()') : '';
			
			var remproperty = $(obj).closest('tr').find("input[name='property\\[\\]']").val();		
			if (( remproperty == '')||( noBaseProperty(remproperty))){ $(obj).closest('tr').remove(); } else { $.alert({title: 'ALERT!', theme: 'dark', type: 'orange', content: 'CAN´T REMOVE A BASE PROPERTY!'}); }
		},
		
		// CHECK FOR NON DUPLICATE PROPERTIES
		checkProperty: function(obj){
			logControl ? console.log(LIB_TITLE + ': checkProperty()') : '';
			var allProperties = $("input[name='property\\[\\]']").map(function(){return $(this).val();}).get();		
			areUnique = allProperties.unique();
			if (allProperties.length !== areUnique.length)  { 
				$.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'DUPLICATE VALUES!'});
				$(obj).val(''); return false;
			} 
			else {
				$(obj).closest('tr').find('.btn-mountable-remove').attr('data-property', $(obj).val() );   
			}
		},
		
		// CHECK PROPERTIES TYPE
		checkType: function(obj){	
			logControl ? console.log(LIB_TITLE + ': checkType()') : '';
			var propType = '';
			var currentTypeValue = $(obj).val();
			var currentType = currentTypeValue.toLowerCase();
			// if type is a valid type, assign this value , if not, string by default.
			propType = $.inArray( currentType, validTypes ) > -1 ?  currentType : 'string';
			$(obj).val(propType);
		},
		
		// CHECK PROPERTIES to be  REQUIRED or NOT 
		checkRequired: function(obj){
			logControl ? console.log(LIB_TITLE + ': checkRequired()') : '';
			var propRequired = '';
			var currentRequiredValue = $(obj).val();
			var currentRequired = currentRequiredValue.toLowerCase();
			// if type is a required field, assign this value , if not, '' by default, (not required).
			propRequired = currentRequired == 'required' ?  currentRequired : '';
			$(obj).val(propRequired);
		},
		
		// CHECK IF A WRITTEN PROPERTY IS OR NOT FROM THE BASE
		noBaseProperty: function(property){
			logControl ? console.log(LIB_TITLE + ': noBaseProperty()') : '';
			
			var isNoBaseProperty = false;
			var noBaseJson = createJsonProperties(JSON.parse(schema)); // to JSON		
			var noBaseProperties = getProperties(noBaseJson); // only Properties Arr
			isNoBaseProperty = $.inArray( property, noBaseProperties ) > -1 ? false : true;
			return isNoBaseProperty;
		},
		
		// DATAMODEL PROPERTIES JSON TO HTML 
		schemaToTable: function(objschema,tableId){
			logControl ? console.log(LIB_TITLE + ': schemaToTable()') : '';
			
			var data, properties, jsonProperties = '';		
		
			// JSON-STRING SCHEMA TO JSON 
			schema = $(objschema).attr('data-schema');
			if 		(typeof schema == 'string'){ data = JSON.parse(schema); }
			else if (typeof schema == 'object'){ data = schema; } else { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'NO TEMPLATE SCHEMA!'}); return false; }
			
			// needs Ontology name and description to Run.
			if (($('#identification').val() == '') || ($('#description').val() == '')){
				$.alert({title: 'ALERT!', theme: 'dark', type: 'orange', content: 'PLEASE, FILL NAME AND DESCRIPTION BEFORE DATAMODEL SELECTION!'}); return false;  
			} 
			else {
				// adding title and description
				// ADD TITLE
				data["title"] = $('#identification').val();
			
				// ADD DESCRIPTION
				if (!data.hasOwnProperty('description')){ data["description"] = $('#description').val(); }
				
				// UDATING SCHEMA STRING
				schema = JSON.stringify(data);
			}
				
			// SCHEMA MODEL ( PROPERTIES / DATOS) 
			if ( data.hasOwnProperty('datos') ){ properties = data.datos; } else { properties = data;  }
			
			// CREATING TABLE FROM DATA.		
			jsonProperties = createJsonProperties(properties);
			
			// CHECK IF WE HAVE A DATAMODEL LOADED YET... o-DO: make confirm.
			if ( $('#datamodel_properties').attr('data-loaded') == 'true' ){		
			
				$.confirm({ title: 'CONFIRM!', theme: 'dark', type: 'orange', content: 'IF YOU CHANGE DATAMODEL YOUR PROPERTIES AND SCHEMA CONFIGURATION WILL BE REMOVE AND STARTED AGAIN!',
					buttons: {
						confirm: function () {
						
							$('#datamodel_properties > tbody').html("");
							$('#datamodel_properties > tbody').append(mountableModel);
							editor.setMode("text");
							editor.setText('{}');
							editor.setMode("tree");
							
							// TO-HTML
							$('#'+tableId).mounTable(jsonProperties,{
								model: '.mountable-model',
								noDebug: false,
								addLine:{				
									button: "#button2",					
									onClick: function (element){
										console.log('Property added!');				
										return true;
									}
								}			
							});
							
							// UPDATING JSON EDITOR
							$('#schema_title').text(data.title + ':');
							editor.setMode("text");
							editor.setText(schema);
							editor.setMode("tree");
							
							// UPDATING FORM FIELDS
							$('#jsonschema').val(schema);
			
							return true;					
						},
						cancel: function () {
							return true;
						}
					}
				});		
			} else {
			
				// TO-HTML
				$('#'+tableId).mounTable(jsonProperties,{
					model: '.mountable-model',
					noDebug: false,
					addLine:{				
						button: "#button2",					
						onClick: function (element){
							console.log('Property added!');				
							return true;
						}
					}			
				});
			
			}		
			
			
			// HIGHLIGHT CURRENT DATAMODEL AND SHOW TABLE
			$('li.mt-list-item.datamodel-template').removeClass('bg-success done');
			$(objschema).closest('li').addClass('bg-success done');
			
			$('#template_schema').removeClass('hide');
			$('#datamodel_properties').attr('data-loaded', true);
			
			//INIT UPDATE SCHEMA
			$('#btn-updateSchema').on('click',function(){ updateSchemaProperties(); });
			
				
			// UPDATING JSON EDITOR
			$('#schema_title').text(data.title + ':');
			editor.setMode("text");
			editor.setText(schema);
			editor.setMode("tree");
			
			// UPDATING DATAMODEL ID for ONTOLOGY
			$('#datamodelid').val($(objschema).attr('data-model'));
			
			// UPDATING FORM FIELDS
			$('#jsonschema').val(schema);
		
		}
		
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// GLOBAL JSON AND CODE EDITOR INSTANCES
	var editor;
	var aceEditor;
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	OntologyCreateController.load(ontologyCreateJson);	
		
	// AUTO INIT CONTROLLER.
	OntologyCreateController.init();
});
