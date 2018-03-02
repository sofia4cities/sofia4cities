var authorizationsArr = []; // add authorizations
var authorizationUpdateArr = []; // get authorizations of the ontology
var authorizationsIds = []; // get authorizations ids for actions
var authorizationObj = {}; // object to receive authorizations responses.
	
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
	var mountableModel2 = $('#ontology_autthorizations').find('tr.authorization-model')[0].outerHTML;
	var validJsonSchema = false;
	var validMetaInf = false;
	var hasId = false; // instance

	
	
	
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
		var updateTypes = $("select[name='type\\[\\]']").map(function(){return $(this).val();}).get();
		var updateRequired = $("select[name='required\\[\\]']").map(function(){return $(this).val();}).get();		
		
		var schemaObj = {};
		
		logControl ? console.log('|--- CURRENT: ' + updateProperties + ' types: ' + updateTypes + ' required: ' + updateRequired): '';
		
		checkUnique = updateProperties.unique();
		if (updateProperties.length !== checkUnique.length)  { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'HAY DUPLICADOS, REVISE!'}); return false; } 
				
		// get current schema 
		if (typeof schema == 'string'){		
			schemaObj = JSON.parse(schema);				
			
		}else if (typeof schema == 'object') { schemaObj = schema; } else { $.alert({title: 'ERROR!', theme: 'dark', type: 'red', content: 'NO TEMPLATE SCHEMA!'}); return false; }
		
		/* // compare properties added with properties on current schema
		// DIFFERENCE BETWEEN BASE vs CURRENT
		jQuery.grep(updateProperties, function(el) {
			if (jQuery.inArray(el, baseArrProperties) == -1) toUpdateProperties.push(el);
				i++;
		});	
		
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
		 $('.nav-tabs li a[href="#tab_4"]').tab('show');		
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
		
		// CLEAN SCHEMA DINAMIC TITLE FROM DATAMODEL SEL.
		$('#schema_title').empty();	
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
		// $('.selectpicker').on('change', function () {
			// $(this).valid();
		// });
		
		// tagsinput validate fix when handleValidation()
		$('#metainf').on('itemAdded', function(event) {
			
			if ($(this).val() !== ''){ $('#metainferror').addClass('hide');}
		});
		
		// authorization tab control 
		$(".nav-tabs a[href='#tab_2']").on("click", function(e) {
		  if ($(this).hasClass("disabled")) {
			e.preventDefault();
			$.alert({title: 'INFO!', type: 'blue' , theme: 'dark', content: 'CREATE ONTOLOGY THEN GIVE AUTHORIZATIONS!'});
			return false;
		  }
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
						
							// Se accede al elemento raiz que referencia el obj
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
		if ($('#metainf').val() === '') { $('#metainferror').removeClass('hide').addClass('help-block-error font-red'); return false;  } else { return true;} 
	}
	
	
	// GENERATE DUMMY ONTOLOGY INSTANCE FROM JSON SCHEMA
	var generateOntologyInstance = function(){
		logControl ? console.log('|--->   generateOntologyInstance()') : ''; 
		
		var instance 		= "";
		var data 			= "";
		var ontologyJson 	= {};
		hasId = false;
		
		// check if json-string can be parsed
		if(IsJsonString(editor.getText())){

			// get JSON
			var ontologyJson = JSON.parse(editor.getText());
			
			instance = instance + "{"
            // for each property on json.properties
			for ( var property in ontologyJson.properties ){
				
				data = "";
				// check for root node
				if ( ontologyJson.properties[property] && ontologyJson.properties[property].$ref ){
				
					if ( !hasId ){	instance = instance + "\"" + property + "\":"; } else {	instance = instance + "\"" + property + "\":";	}
				
					// access node root reference
					var ref = ontologyJson.properties[property].$ref;
					ref = ref.substring(ref.indexOf("/")+1, ref.length);
					data = ontologyJson[ref].properties;
				
					// Se genera la seccion correspondiente a cada propiedad del elemento de referencia
					instance = instance + "{ ";    
					for( var propertyName in data ) {
						instance = generateProperty(propertyName, data[propertyName], instance);
					}
					instance = instance.substring(0,instance.length-1);
					instance = instance + "}";
				} 
				else {
					// if no root node, get from main properties.
					instance = generateProperty(property, ontologyJson.properties[property], instance);
					instance = instance.substring(0,instance.length-1);
				}
				
				instance = instance + ",";
			}
			
			instance = instance.substring(0,instance.length-1);  
			instance = instance + "}";                      
			document.getElementById("ontology_instance").innerHTML = instance;
			
			if (ontologyJson.properties == null ){
                	document.getElementById("ontology_instance").innerHTML = "";
			}	
		
		}
		else {
			// no JSON no fun!
			$.alert({title: 'JSON SCHEMA!', type: 'red' , theme: 'dark', content: 'NO SCHEMA'});
		}		
	}
	
	
	// GENERATE EACH PROPERTY FOR ONTOLOGY INSTANCE.
	var generateProperty = function(propertyName, property, instance){
    	logControl ? console.log('    |--->   generateProperty()') : '';
		var thevalue = "";
		
		// if has internalID (oid) we generate it.
        if ( propertyName == "$oid") {
            hasId = true;
            if ( property.type == "string") { instance = instance + "\"$oid\":\"53b281b1c91cbd35025e3d91\""; }
            instance = instance + ",";        
        }
		else {
			// if not oid, we treat the property        	
            instance = instance + "\"" + propertyName + "\":"; // adding name
            
            var tipo = property.type; // adding type
            if (propertyName == "geometry"){ instance = instance + generateBasicType("geometry", "", "");
            // adding object type
            } else if (tipo.toLowerCase() == "object"){ instance = instance + generateObject(property, "", propertyName);
			// adding array type
            } else if (tipo.toLowerCase() == "array" ){ instance = instance + generateArray(property, "", propertyName);
            // else basic type
            } else {
                thevalue = "";
                // if enum type, get first value of enum.
                if ( property.enum != null ){ thevalue = property.enum[0]; }
                instance = instance + generateBasicType(tipo, "", "", thevalue);
            }
            instance = instance + ",";
        }
        return instance;
    }
	
	
	// GENERARATE PROPERTY TYPES [GEOMETRY, OBJECT, ARRAY OR BASIC]
	var generateBasicType = function(propType, obj, parent, thevalue){
		logControl ? console.log('        |--->   generateBasicType()') : '';
		
    	// if enum, return enum value
    	if (thevalue != null && thevalue != ""){ return "\"" + thevalue + "\""; }
    	// string
        if (propType == "string") {        	
        	if 		(parent == "media" && obj == "storageArea")	  { return "\"SERIALIZED\""; } 
			else if (parent == "media" && obj == "binaryEncoding"){ return "\"Base64\""; }
			else if (parent == "media" && obj == "name")		  {	return "\"fichero.pdf\""; } 
			else if (parent == "media" && obj == "mime")		  {	return "\"application/pdf\""; }
			else{ return "\"string\""; }			
        }
		// integer,boolean, object, number and geometry
		else if ( propType == "integer" ){ return "1" } 		
		else if ( propType == "boolean" ){ return "true"; }
		else if ( propType == "object" ){ return "{}"; }
		else if ( propType == "geometry" || obj =="geometry"){ return "{\"type\":\"Point\", \"coordinates\":[9,19.3]}"; }
		else if ( propType == "number"   || propType == "numeric" ){	return "28.6" } 		
    }	
	
	
	// GENERARATE PROPERTY TYPES [GEOMETRY, OBJECT, ARRAY ]
	var generateObject = function(ontology, instance, parent){
        logControl ? console.log('        |--->   generateObject()') : '';
		
       	instance = "{";       	
       	if ( ontology.properties ){
	        for ( var obj in ontology.properties ){
	            
				var objtype = ontology.properties[obj].type;
	             // if obj <> date or geometry, iterates recursive for treatment.
	             if ((objtype.toLowerCase() == "object") && (obj != "geometry") && ontology.properties[obj].properties && ontology.properties[obj].properties.$date == null ){ 
	             	instance = instance + "\"" +obj+"\":"+ generateObject(ontology.properties[obj], "", obj);
	             
	             }
				 // date obj
				 else if ((ontology.properties && ontology.properties.$date != null) || (ontology.properties && ontology.properties[obj] && ontology.properties[obj].properties && ontology.properties[obj].properties.$date!= null)){
	                 // date root node or date children node
	            	 if (obj == "$date"){  instance = instance + "\"$date\": \"2014-01-30T17:14:00Z\""; } else { instance = instance + "\"" +obj+"\":"+ "{\"$date\": \"2014-01-30T17:14:00Z\"}"; }
				 }
				 // geometry with direct reference to point
				 else if (ontology[obj] && ontology[obj].properties &&  ontology[obj].properties[propertyName].properties && ontology[obj].properties[propertyName].properties.type && ontology.properties[obj].properties.type.enum[0]== "Point"){
	                 instance = instance + "\"" +obj+"\":"+ "{\"type\":\"Point\", \"coordinates\":[9,19.3]}";
	             
	             }
				// array
				 else if (objtype.toLowerCase() == "array"){
	                    instance = instance + "\""+ obj + "\":" + generateArray(ontology.properties[obj], "", obj);	             
	             }
				 // Basic
				 else {
	            	 var valor = "";
	            	 // if enum getr first value
	                 if (ontology.properties[obj].enum != null){
	                	  valor = ontology.properties[obj].enum[0];
	                 }
	            	 instance = instance + "\""+ obj + "\":" + generateBasicType(objtype, obj, parent, valor);
	             }
	             instance = instance + ",";
	        }
	        instance = instance.substring(0,instance.length-1);
	     // if obj is null, generate default
       	} else {
       		instance = instance + "\"object\"";
       	}
        return instance + "}";
    }
		
	// GENERARATE PROPERTY TYPES [ ARRAY ]
	var generateArray = function(ontology, instance, parent){
		 logControl ? console.log('        |--->   generateArray()') : '';
        var minItems = 1;
        // Se obtiene el numero minimo de elementos del array
        if (ontology.minItems != null) {
            minItems =  ontology.minItems;
			
        }
        instance = instance + "[";        
        if (ontology.items.type.toLowerCase() == "object"){
            for (i=1;i<=minItems;i++) {
                instance = instance + generateObject(ontology.items, "", parent);
                if (i < minItems){
                    instance = instance + ",";
                }
            }       
        } else {
            for (i=1;i<=minItems;i++) {
                var valor ="";
                if (ontology.items.enum != null){
                    valor = ontology.items.enum[0];
                }
                instance = instance + generateBasicType(ontology.items.type, "", "", valor);
                if (i < minItems){
                    instance = instance + ",";
                }
            }
        }
        return instance + "]";  
    };    
	
	// AJAX AUTHORIZATION FUNCTIONS
	var authorization = function(action,ontology,user,accesstype,authorization,btn){
		logControl ? console.log('|---> authorization()') : '';	
		var insertURL = '/controlpanel/ontologies/authorization';
		var updateURL = '/controlpanel/ontologies/authorization/update';
		var deleteURL = '/controlpanel/ontologies/authorization/delete';
		var response = {};
		
		if (action === 'insert'){
			console.log('    |---> Inserting... ' + insertURL);
						
			$.ajax({
				url:insertURL,
				type:"POST",
				async: true,
				data: {"accesstype": accesstype, "ontology": ontology,"user": user},			 
				dataType:"json",
				success: function(response,status){							
					
					var propAuth = {"users":user,"accesstypes":accesstype,"id": response.id};
					authorizationsArr.push(propAuth);
					console.log('     |---> JSONtoTable: ' + authorizationsArr.length + ' data: ' + JSON.stringify(authorizationsArr));
					// store ids for after actions.	inside callback 				
					var user_id = user;
					var auth_id = response.id;
					var AuthId = {[user_id]:auth_id};
					authorizationsIds.push(AuthId);
					console.log('     |---> Auths: ' + authorizationsIds.length + ' data: ' + JSON.stringify(authorizationsIds));
										
					// TO-HTML
					if ($('#authorizations').attr('data-loaded') === 'true'){
						$('#ontology_autthorizations > tbody').html("");
						$('#ontology_autthorizations > tbody').append(mountableModel2);
					}
					console.log('authorizationsArr: ' + authorizationsArr.length + ' Arr: ' + JSON.stringify(authorizationsArr));
					$('#ontology_autthorizations').mounTable(authorizationsArr,{
						model: '.authorization-model',
						noDebug: false							
					});
					
					// hide info , disable user and show table
					$('#alert-authorizations').toggle($('#alert-authorizations').hasClass('hide'));			
					$("#users").selectpicker('deselectAll');
					$("#users option[value=" + $('#users').val() + "]").prop('disabled', true);
					$("#users").selectpicker('refresh');
					$('#authorizations').removeClass('hide');
					$('#authorizations').attr('data-loaded',true);
					
				}
			});

	
		}
		if (action === 'update'){
			
			$.ajax({url:updateURL, type:"POST", async: true, 
				data: {"id": authorization, "accesstype": accesstype},			 
				dataType:"json",
				success: function(response,status){
							
					var updateIndex = foundIndex(user,'users',authorizationsArr);			
					authorizationsArr[updateIndex]["accesstypes"] = accesstype;
					console.log('ACTUALIZADO: ' + authorizationsArr[updateIndex]["accesstypes"]);					
				}
			});
			
			
		}
		if (action  === 'delete'){
			console.log('    |---> Deleting... ' + user + ' with authId:' + authorization );
			
			$.ajax({url:deleteURL, type:"POST", async: true, 
				data: {"id": authorization},			 
				dataType:"json",
				success: function(response,status){									
					
					// remove object
					var removeIndex = authorizationsIds.map(function(item) { return item[user]; }).indexOf(authorization);			
					authorizationsIds.splice(removeIndex, 1);
					authorizationsArr.splice(removeIndex, 1);
					
					console.log('AuthorizationsIDs: ' + JSON.stringify(authorizationsIds));
					// refresh interface. TO-DO: EL this este fallará					
					if ( response  ){ 
						$(this).closest('tr').remove();
						$("#users option[value=" + $('#users').val() + "]").prop('disabled', false);
					}
					else{ 
						$.alert({title: 'ALERT!', theme: 'dark', type: 'orange', content: 'VACIO!!'}); 
					}
				}
			});			
		}	
	};
	
	// return position to find authId.
	var foundIndex = function(what,item,arr){
		var found = '';
		arr.forEach(function(element, index, array) {
			if ( what === element[item]){ found = index;  console.log("a[" + index + "] = " + element[item] + ' Founded in position: ' + found ); } 
			
		});		
		return found;
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
		},
		
		// GENERATE DUMMY ONTOLOGY INSTANCES
		generateInstance: function(){
			logControl ? console.log(LIB_TITLE + ': generateInstance()') : '';
			generateOntologyInstance();
		},
		
		// INSERT AUTHORIZATION
		insertAuthorization: function(){
			logControl ? console.log(LIB_TITLE + ': insertAuthorization()') : '';
			if ( ontologyCreateReg.actionMode !== null){	
				// UPDATE MODE ONLY AND VALUES on user and accesstype
				if (($('#users').val() !== '') && ($("#users option:selected").attr('disabled') !== 'disabled') && ($('#accesstypes').val() !== '')){
					
					// AJAX INSERT (ACTION,ONTOLOGYID,USER,ACCESSTYPE) returns object with data.
					authorization('insert',ontologyCreateReg.ontologyId,$('#users').val(),$('#accesstypes').val(),'');
								
				}	
			}
		},
		
		// REMOVE authorization
		removeAuthorization: function(obj){
			logControl ? console.log(LIB_TITLE + ': removeAuthorization()') : '';
			if ( ontologyCreateReg.actionMode !== null){
				
				// AJAX REMOVE (ACTION,ONTOLOGYID,USER,ACCESSTYPE) returns object with data.
				var selUser = $(obj).closest('tr').find("input[name='users\\[\\]']").val();
				var selAccessType = $(obj).closest('tr').find("select[name='accesstypes\\[\\]']").val();				
				
				var removeIndex = foundIndex(selUser,'users',authorizationsArr);				
				var selAuthorizationId = authorizationsIds[removeIndex][selUser];
				
				console.log('removeAuthorization:' + selAuthorizationId);
				
				authorization('delete',ontologyCreateReg.ontologyId, selUser, selAccessType, selAuthorizationId );				
			}
		},
		
		// UPDATE authorization
		updateAuthorization: function(obj){
			logControl ? console.log(LIB_TITLE + ': updateAuthorization()') : '';
			if ( ontologyCreateReg.actionMode !== null){
				
				// AJAX UPDATE (ACTION,ONTOLOGYID,USER,ACCESSTYPE,ID) returns object with data.
				var selUser = $(obj).closest('tr').find("input[name='users\\[\\]']").val();
				var selAccessType = $(obj).closest('tr').find("select[name='accesstypes\\[\\]']").val();
								
				var updateIndex = foundIndex(selUser,'users',authorizationsArr);				
				var selAuthorizationId = authorizationsIds[updateIndex][selUser];				
				
				console.log('updateAuthorization:' + selAuthorizationId);
				
				if (selAccessType !== authorizationsArr[updateIndex]["accesstypes"]){
					authorization('update',ontologyCreateReg.ontologyId, selUser, selAccessType, selAuthorizationId);
				} 
				else { console.log('no hay cambios');}
			}
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
