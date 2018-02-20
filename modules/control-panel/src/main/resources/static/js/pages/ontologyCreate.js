var OntologyCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Ontology Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	var validTypes = ["string","object","number","date","timestamp","array","binary"]; // Valid property types	
	var mountableModel = $('#datamodel_properties').find('tr.mountable-model')[0].outerHTML; // save html-model for when select new datamodel, is remove current and create a new one.
	var validJsonSchema = false;
	var validMetaInf = false;
	
	
	
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
	
	
	// AUX. getTypes return types array
	var getTypes = function(json){	
		logControl ? console.log('   |---  getTypes()') : '';
		var arrTypes = [];
			
		// KEYs
		$.each( json, function (key, object){						
			$.each(object, function (key, value){
				if (value){ if ( key == 'type') {  arrTypes.push(value); } } 
			});			
		});			
		logControl ? console.log('      |----- getTypes: ' + JSON.stringify(arrTypes)) : '';
		return arrTypes;	
	}
	
	
	// AUX. getRequired return required array
	var getRequired = function(json){	
		logControl ? console.log('   |---  getReguired()') : '';
		var arrRequired = [];
			
		// KEYs
		$.each( json, function (key, object){						
			$.each(object, function (key, value){
				if (value){ if ( key == 'required') { if (value == '') {value='none'} arrRequired.push(value); } } 
			});			
		});			
		logControl ? console.log('      |----- getRequired: ' + JSON.stringify(arrRequired)) : '';
		return arrRequired;	
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
		
		/* // compare properties added with properties on current schema
		baseJson = createJsonProperties(schemaObj);
				
		// schema string -> Object --> update --> toString --> to editor.
		baseArrProperties	= getProperties(baseJson);
		baseArrRequired 	= getRequired(baseJson);
		baseArrTypes 		= getTypes(baseJson);
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
		
		
		jQuery.grep(updateTypes, function(el) {
			if (jQuery.inArray(el, baseArrTypes) == -1) toUpdateTypes.push(el);
				i++;
		});	
		
		logControl ? console.log(" |-------------  the difference are " + toUpdateTypes + ' elements: ' + toUpdateTypes.length): '';
		
		
		
		jQuery.grep(updateRequired, function(el) {
			if (jQuery.inArray(el, baseArrRequired) == -1) toUpdateRequired.push(jQuery.inArray(el, baseArrRequired));
				i++;
		});	
		
		logControl ? console.log(" |-------------  the difference are " + toUpdateRequired + ' elements: ' + toUpdateRequired.length): '';
		 */
		
		// UPDATE SCHEMA		
		// UPDATE ALL PROPERTIES EACH TIME.
		if ( updateProperties.length ){	
			$.each(updateProperties, function( index, value ) {
				propIndex = updateProperties.indexOf(value);
				logControl ? console.log('index: ' + propIndex + ' | property: ' + updateProperties[propIndex] + ' type: ' + updateTypes[propIndex] + ' required: ' + updateRequired[propIndex]) : '';
					// update property on Schema /current are stored in schema var.  (property,type,required)
					updateProperty(updateProperties[propIndex], updateTypes[propIndex], updateRequired[propIndex] );					
			});			
		}
		
		// CHANGE TO SCHEMA TAB.
		 $('.nav-tabs li a[href="#tab_2"]').tab('show');		
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
		if (req == 'required') {  
			if (jQuery.inArray(prop, requires) == -1) requires.push(prop);
		} 		
		
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
	
	
	// CHECK IF A WRITTEN PROPERTY IS OR NOT FROM THE BASE
	var	noBaseProperty =  function(property){
			logControl ? console.log(LIB_TITLE + ': noBaseProperty()') : '';
			
			var isNoBaseProperty = false;
			var noBaseJson = createJsonProperties(JSON.parse(schema)); // to JSON		
			var noBaseProperties = getProperties(noBaseJson); // only Properties Arr
			isNoBaseProperty = $.inArray( property, noBaseProperties ) > -1 ? false : true;
			return isNoBaseProperty;
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
			// CLEAN ALL EXCEPTS cssClass "no-remove" persistent fields
			if(!$(this).hasClass("no-remove")){$(this).val('');}
		});
		
		//CLEANING SELECTs
		$(".selectpicker").each(function(){
			$(this).val( '' );
			$(this).selectpicker('deselectAll').selectpicker('refresh');
		});
		
		//CLEANING CHECKS
		$('input:checkbox').not('.no-remove').removeAttr('checked');
		
		// CLEANING tagsinput
		$('.tagsinput').tagsinput('removeAll');
		
		// CLEAN ALERT MSG
		$('.alert-danger').hide();
		
		// CLEAN DATAMODEL TABLE
		$('#datamodel_properties').attr('data-loaded',false);
		$('#datamodel_properties > tbody').html("");
		$('#datamodel_properties > tbody').append(mountableModel);
		editor.setMode("text");
		editor.setText('{}');
		editor.setMode("tree");
		$('li.mt-list-item.datamodel-template').removeClass('bg-success done');
		$('.list-toggle-container').not('.collapsed').trigger('click');
		$('#template_schema').addClass('hide');
		
		
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
            ignore: ":hidden:not('.selectpicker, .hidden-validation')", // validate all fields including form hidden input but not selectpicker
			lang: currentLanguage,
			// custom messages
            messages: {	
				jsonschema: { required:"El esquema no se ha guardado correctamente"},
				datamodelid: { required: "Por favor seleccione una plantilla de ontología, aunque sea la vacia."}
			},
			// validation rules
            rules: {
				ontologyId:		{ minlength: 5, required: true },
                identification:	{ minlength: 5, required: true },						
				datamodelid:	{ required: true},
				jsonschema:		{ required: true},
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
				else if ( element.is(':hidden'))	{ 
					if ($('#datamodelid').val() === '') { $('#datamodelError').removeClass('hide');} 					
				}				
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
               
                error1.hide();
				// VALIDATE JSON SCHEMA 
				validJsonSchema = validateJsonSchema();				
				if (validJsonSchema){
					
					// VALIDATE TAGSINPUT
					validMetaInf = validateTagsInput();
					if (validMetaInf) {
						form.submit();					
					}
				}
				else {
					success1.hide();
					error1.show();										
				}
				
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
		
		// tagsinput validate fix when handleValidation()
		$('#metainf').on('itemAdded', function(event) {
			
			if ($(this).val() !== ''){ $('#metainferror').addClass('hide');}
		});
				
		
		// 	INPUT MASK FOR ontology identification allow only letters, numbers and -_
		$("#identification").inputmask({ regex: "[a-zA-Z0-9_-]*", greedy: false });
		
		// Reset form
		$('#resetBtn').on('click',function(){ 
			cleanFields('ontology_create_form');
		});

		// UPDATE TITLE AND DESCRIPTION IF CHANGED 
		$('#identification').on('change', function(){
			var jsonFromEditor = {};
			var datamodelLoaded = $('#datamodel_properties').attr('data-loaded');
			if (datamodelLoaded){			
				if (IsJsonString(editor.getText())){				
					jsonFromEditor = editor.get();
					jsonFromEditor["title"] = $(this).val();
					editor.set(jsonFromEditor);
				}			
			}		
		});
	
		$('#description').on('change', function(){
			var jsonFromEditor = {};
			var datamodelLoaded = $('#datamodel_properties').attr('data-loaded');
			if (datamodelLoaded){			
				if (IsJsonString(editor.getText())){				
					jsonFromEditor = editor.get();
					jsonFromEditor["description"] = $(this).val();
					editor.set(jsonFromEditor);
				}			
			}	
			
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
	
	
	// DATAMODEL TEMPLATE COUNTERS 
	var dataModeltemplateCounters = function(){
		
		var datamodels = $('.datamodel-types');
		datamodels.each(function(ind,elem){ 
			var templates = $(elem).find('ul.datamodel-template').length;			
			var typeHref = $(elem).find('a.list-toggle-container > div.list-toggle');
			$('<span class="pull-right badge badge-default">'+ templates +'</span>').appendTo(typeHref);
			if (templates == 0 ) { $(elem).find('div.list-toggle').removeClass('bg-grey-mint').addClass('bg-grey-steel font-grey-cascade');	}			
		});
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
			},
			onChange: function(){
				
				console.log('se modifica el editor en modo:' + editor.mode + ' contenido: ' + editor.getText());
			}
		};
		
		editor = new jsoneditor.JSONEditor(container, options, "");		
		
	}
	
	
	// CHECK IF JSON STRING WHEN JSON PARSE IS OK OR NOT, IF THE JSON IS MALFORMED THE JSON.parse() is broken.
	var IsJsonString = function(str) {
		try {
			JSON.parse(str);
		}
		catch (e) {	return false; }
		
		return true;
	}
	
	// JSON SCHEMA VALIDATION PROCESS
	var validateJsonSchema = function(){
        logControl ? console.log('|--->   validateJsonSchema()') : ''; 
		
		if(IsJsonString(editor.getText())){
			
			var isValid = true;
		 
			// obtener esquemaOntologiaJson
			var ontologia = JSON.parse(editor.getText());
			
			if((ontologia.properties == undefined && ontologia.required == undefined)){
			
				$.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'REQUIRED OR PROPERTIES NO EXISTS ERROR'});
				isValid = false;
				return isValid;
				
			}else if( ontologia.properties == undefined && (ontologia.additionalProperties == null || ontologia.additionalProperties == false)){
			
				$.alert({title: 'ERROR JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'NO PROPERTIES!'});
				isValid = false;
				return isValid;
					
			}else{  
			
				// Situarse en elemento raiz  ontologia.properties (ontologia) o ontologia.datos.properties (datos)
				var nodo;
				
				if(jQuery.isEmptyObject(ontologia.properties)){
					 //esquema sin raiz
					 nodo=ontologia;
				}else{
					for (var property in ontologia.properties){
						
						var data = "";
						//Se comprueba si dispone de un elemento raiz
						if (ontologia.properties[property] && ontologia.properties[property].$ref){
						
							// Se accede al elemento raiz que referencia el objeto
							var ref = ontologia.properties[property].$ref;
							ref = ref.substring(ref.indexOf("/")+1, ref.length);
							nodo = ontologia[ref];
							
						} else {
							//esquema sin raiz
							nodo = ontologia;
						}
					}
				}				
				// Plantilla EmptyBase: se permite crear/modificar si se cumple alguna de estas condiciones:
				//a.     Hay al menos un campo (requerido o no requerido)
				//b.     No hay ningún campo (requerido o no requerido) pero tenemos el AditionalProperties = true
				// Resto de casos: Con que haya al menos un campo (da igual que sea requerido o no requerido) o el AditionalProperties = true, se permite crear/actualizar el esquema de la ontología.
				
				// Nodo no tiene valor
				if( (nodo == undefined)){
					   
					 $.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'NO NODE!'});
					  isValid = false;
					  return isValid;
					  
				// Propiedades no definida y additionarProperteis no esta informado a true     
				}else  if(  (nodo.properties ==undefined || jQuery.isEmptyObject(nodo.properties))  && (nodo.additionalProperties == null || nodo.additionalProperties == false)){
					
					$.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'PROPERTIES NO DEFINED!'});
					isValid = false;
					return isValid;
				}
			   
			   
				
				//Validaciones sobre propiedas y requeridos
				else if(nodo.required!=undefined && (nodo.additionalProperties == null || nodo.additionalProperties == false)) {

					var requiredData = nodo.required.length;
					
					// Si tiene elementos requeridos
					if (requiredData!=null && requiredData>0){
					
						   if(nodo.properties!=null){
								 var propertiesNumber=0;
								 for(var propertyName in nodo.properties) {
									 propertiesNumber++;
								  }
								 if(propertiesNumber==0){
									$.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'REQUIRED PROPERTIES ERROR'});
									isValid = true;
								 }
						}
						else{
							$.alert({title: 'JSON SCHEMA !', type: 'red' , theme: 'dark', content: 'NO PROPERTIES!'});
							isValid = false;
							return isValid;
						}			
					}           
				}             
			}
		}
		else {
			// no schema no fun!
			isValid = false;
			$.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'NO SCHEMA'});			
			return isValid;
			
		}
		
		
		
		console.log('JSON SCHEMA VALIDATION: ' + isValid);
		return isValid;
	}	
	
	
	// VALIDATE TAGSINPUT
	var validateTagsInput = function(){		
		if ($('#metainf').val() === '') { $('#metainferror').removeClass('hide').addClass('help-block-error'); console.log('metainf ERROR'); return false;  } else { console.log('metainf OK'); return true;} 
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
			dataModeltemplateCounters();
			
			
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
				$.alert({title: ontologyCreateReg.datamodel, theme: 'dark', type: 'orange', content: ontologyCreateReg.dataModelSelection}); return false;  
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
			
			// HIDE ERROR FOR DATAMODEL NOT SELECTED IF IT WAS VISIBLE
			$('#datamodelError').addClass('hide');
		
		},
				
		// JSON SCHEMA VALIDATION
		validateJson: function(){
				
			validateJsonSchema();			
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// GLOBAL JSON AND CODE EDITOR INSTANCES
	var editor;
	var aceEditor;
	var schema = ''; // current schema json string var
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	OntologyCreateController.load(ontologyCreateJson);	
		
	// AUTO INIT CONTROLLER.
	OntologyCreateController.init();
});
