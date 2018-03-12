var ApiCustomOpsController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Menu Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.
	var currentFormat = '' // date format depends on currentLanguage.
	var internalFormat = 'yyyy/mm/dd';
	var internalLanguage = 'en';
	
	var name_op_edit_customsql=''
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
    function selectEditOp(field) {
		name_op_edit_customsql = field;
		loadCustomSql (name_op_edit_customsql);
    }
	
    function validateNameOp(field) {
        var RegExPattern = /^[a-zA-Z0-9._-]*$/;
        if ((field.value.match(RegExPattern))) {

        } else {
        	ApiCreateController.showErrorDialog('Error', apiCustomOpsReg.apimanager_customsql_error_name_format);
        }
    }
    
    function loadParamQuerySQLType() {
   	 var query = document.getElementById("id_query_op_customsql").value;
        var error = "";
        error = isValidQuery(query);
        if (error==""){
       	    clearParams();
       	    showParams(query);
        } else {
        	ApiCreateController.showErrorDialog('Error', error);
        }
    }

    function loadParamsFromQuery(field, op_name) {
        clearParams();
        if (op_name==null || op_name==""){
            var error = "";
            if (field != null && field !=""){
                error = isValidQuery(field);
            }
            if (error==""){
            	showParams(field);
            } else {
            	ApiCreateController.showErrorDialog('Error', error);
            }
        } else {
        	showParams(field);
        }
    }
    
    function isValidQuery(field){
    	if (field != null && field !=""){
    		if (((field.toUpperCase().indexOf("SELECT")>=0)&&($('#id_customsql_querytype').val()=="SQLLIKE"))||
    			((field.toUpperCase().indexOf("DB.")>=0)&&($('#id_customsql_querytype').val()=="NATIVE"))){
    			if ($("#ontology option:selected").text()!= "" && field.indexOf($("#ontology option:selected").text())>=0){
    				if (((field.split("{$").length - 1)==(field.split("}").length - 1) || ($('#id_customsql_querytype').val()=="NATIVE"))){
    					return "";
    				} else {
    					return (apiCustomOpsReg.apimanager_customsql_error_query_params);
    				}
    			} else {
    				return (apiCustomOpsReg.apimanager_customsql_error_query_ontology);
    			}
    		} else {
    			return (apiCustomOpsReg.apimanager_customsql_error_query);
    		}
    	} else {
    		return (apiCustomOpsReg.apimanager_customsql_error_required);
    	}
    }

    function clearParams() {
    	$("#customsql_paramsquery").html("");
    	$("#customsql_params_div").css({ 'display': "none" });
    	$("#customsql_noparam_div").css({ 'display': "block" });
    }

    function showParams(query) {
		 var param = "";
		 customsql_queryparam = new Array();
		 while (query.indexOf("{$")>0 && query.indexOf("}")!=-1){
			 var param = query.substring(query.indexOf("{$") + 2, query.indexOf("}", query.indexOf("{$")));
		
			 if (param.indexOf(":")==-1){
				 loadParamQuery(param);
				 query = query.substring(query.indexOf("}", query.indexOf("{$")) + 1);
			 } else {
			    query = query.substring(query.indexOf("{$") + 2, query.length);
			 }
		 }
		 if (customsql_queryparam.length>0){
			$("#customsql_noparam_div").css({ 'display': "none" });
			$("#customsql_params_div").css({ 'display': "block" });
		 } else {
		 	$("#customsql_params_div").css({ 'display': "none" });
		 	$("#customsql_noparam_div").css({ 'display': "block" });
		 }
    }

    function loadParamQuery(param) {
        var customsqlParamaDiv=document.getElementById("customsql_paramsquery");

        var newCustomsqlParamDiv = document.createElement('div');
        newCustomsqlParamDiv.id= "customsql_param_" + param;

        var newCustomsqlParamFieldSet = document.createElement('fieldset');
        newCustomsqlParamFieldSet.id = "customsql_param_fieldset" + param;

        newCustomsqlParamFieldSet.style.margin="10px";
        newCustomsqlParamFieldSet.style.marginTop="10px";
        newCustomsqlParamFieldSet.style.padding="10px";
        newCustomsqlParamFieldSet.style.border="1px #d0d2d9 dotted";
        newCustomsqlParamFieldSet.style.display="inline";

        var newLabelCustomsqlParam = document.createElement('label');
        newLabelCustomsqlParam.id = param;
        newLabelCustomsqlParam.className="description";
        newLabelCustomsqlParam.style.marginRight="20px";
        newLabelCustomsqlParam.innerHTML=param;

        newCustomsqlParamFieldSet.appendChild(newLabelCustomsqlParam);

        var newInputCustomsqlParam = document.createElement('select');
        newInputCustomsqlParam.id="customsqlParamType_" + param;
        newInputCustomsqlParam.style.cssFloat="right";

        var optionString = document.createElement( 'option' );
        optionString.value = "string"; 
        optionString.text = "STRING";
        newInputCustomsqlParam.add(optionString);
        var optionNumber = document.createElement( 'option' );
        optionNumber.value = "number"; 
        optionNumber.text = "NUMBER";
        newInputCustomsqlParam.add(optionNumber);
        var optionDate = document.createElement( 'option' );
        optionDate.value = "date"; 
        optionDate.text = "DATE";
        newInputCustomsqlParam.add(optionDate);

        newCustomsqlParamFieldSet.appendChild(newInputCustomsqlParam);

        customsqlParamaDiv.appendChild(newCustomsqlParamFieldSet);

        var parameter = {name: param, condition: "REQUIRED", dataType: document.getElementById("customsqlParamType_" + param).value, value: document.getElementById("customsqlParamType_" + param).value  ,description: ""};
        customsql_queryparam.push(parameter);
    }

    function saveCustomsqlOperation(){
        var id_type_op_customsql = $('#id_type_op_customsql').val();
        var id_name_op_customsql = $('#id_name_op_customsql').val();
        var errorQuery = isValidQuery($('#id_query_op_customsql').val());

        var desc_op_customsql = $('#id_desc_op_customsql').val();
        if (id_type_op_customsql!=null && id_type_op_customsql!="" && id_name_op_customsql!=null && id_name_op_customsql!="" && desc_op_customsql!=null && desc_op_customsql!=""){
       	 if (errorQuery!=null && errorQuery==""){
	             if (name_op_edit_customsql==null || name_op_edit_customsql==""){
	                 if (!ApiCreateController.existOperation(id_name_op_customsql)){
	                     var querystrings = new Array();
	                     var headers = new Array();
	                     var operation = {identification: id_name_op_customsql, description: desc_op_customsql , operation: id_type_op_customsql, path: "", querystrings: querystrings, headers: headers};

	                     saveParamQueryCustomsql(operation);

	                     addOperationCustomsql(operation);

	                     operations.push(operation);

	                     $('#dialog-customsql').modal('toggle');
	                 } else {
	                	 ApiCreateController.showErrorDialog('Error', apiCustomOpsReg.apimanager_customsql_error_operation_exists);
	                 }
	             } else {
	                 for(var i=0; i<operations.length; i+=1){
	                     if (operations [i].identification == name_op_edit_customsql){
	                    	 operations [i].description=desc_op_customsql;
	                    	 operations [i].operation=id_type_op_customsql;

	                    	 operations [i].querystrings = new Array();

	                         saveParamQueryCustomsql(operations [i]);
	                         break;
	                     }
	                 }
	                 updateCustomSqlOperation(operations[i]);
	                 $('#dialog-customsql').modal('toggle');
	             }
	         } else {
	        	 ApiCreateController.showErrorDialog('Error', errorQuery);
	         }
        } else {
        	ApiCreateController.showErrorDialog('Error', apiCustomOpsReg.apimanager_customsql_error_fields);
        }
    }

    function saveParamQueryCustomsql(operation){
   	 	var queryParameter = {name: "$query", condition: "CONSTANT", dataType: "string", value: $('#id_query_op_customsql').val() , description: "", headerType: "query"};
   	 	operation.querystrings.push(queryParameter);
        var targetBDParameter = {name: "$targetdb", condition: "CONSTANT", dataType: "string", value: $('#id_customsql_targetBD').val() , description: "", headerType: "query"};
        operation.querystrings.push(targetBDParameter);
        var querytypeBDParameter = {name: "$queryType", condition: "CONSTANT", dataType: "string", value: $('#id_customsql_querytype').val() , description: "", headerType: "query"};
        operation.querystrings.push(querytypeBDParameter);
        var path = "\\" + operation.identification;
        if (customsql_queryparam.length>0){
       	 	path=path + "?";
        }
        for (var i = 0; i < customsql_queryparam.length; i++) {
	       	customsql_queryparam [i].value = $('#customsqlParamType_' + customsql_queryparam [i].name).val();
	       	customsql_queryparam [i].dataType = $('#customsqlParamType_' + customsql_queryparam [i].name).val();
	       	customsql_queryparam [i].headerType = "query";
	       	operation.querystrings.push(customsql_queryparam [i]);
	       	path = path + "$" + customsql_queryparam [i].name + "={" + customsql_queryparam [i].name +"}";
	       	if (i < customsql_queryparam.length-1){
	       		path = path + "&";
	       	}
        }
        operation.path = path;
    }


    function addOperationCustomsql(operation){
   	 var customsqlOpsDiv=document.getElementById("divCUSTOMSQLS");

        var newCustomsqlParamDiv = document.createElement('div');
        newCustomsqlParamDiv.id= operation.identification;
        newCustomsqlParamDiv.className= "op_div_selected";

        var newInputCustomsqlOperationDiv = document.createElement('div');
        newInputCustomsqlOperationDiv.className= "op_button_div";
        
		
		// div description get all the data inside
		var OperationDivDesc = document.createElement('div');
		OperationDivDesc.className = "op_desc_div";
		
		
        var newInputCustomsqlOperation = document.createElement('input');
			newInputCustomsqlOperation.id=operation.identification + "_OPERATION";
			newInputCustomsqlOperation.className="op_button_selected";        
			newInputCustomsqlOperation.type="reset";
			newInputCustomsqlOperation.value=apiCustomOpsReg.apimanager_customBtn;
			newInputCustomsqlOperation.name="CUSTOM_SQL";
			newInputCustomsqlOperation.disabled="disabled";
			newInputCustomsqlOperationDiv.appendChild(newInputCustomsqlOperation);
			newCustomsqlParamDiv.appendChild(newInputCustomsqlOperationDiv);
		
		// CONTENTS, ALL INSIDE DESC , THEN INSIDE customsqlOpsDiv
        var newLabelCustomsqlOperation = document.createElement('label');
			newLabelCustomsqlOperation.id=operation.identification + "_LABEL";
			newLabelCustomsqlOperation.className="description bold";        
			newLabelCustomsqlOperation.style = "font-size: 14px; color: rgb(34, 48, 77); padding-right:15px; min-width: 200px; display: inline-block";
			newLabelCustomsqlOperation.innerHTML=operation.identification;

        //newCustomsqlParamDiv.appendChild(newLabelCustomsqlOperation);
		OperationDivDesc.appendChild(newLabelCustomsqlOperation);
		

        var newInputEditCustomsqlOperation = document.createElement('input');
			newInputEditCustomsqlOperation.id=operation.identification + "_Edit";
			newInputEditCustomsqlOperation.className="btn btn-sm blue-hoki";
			newInputEditCustomsqlOperation.style = "float: right; position: relative;top: -40px";
			newInputEditCustomsqlOperation.type="button";      
			newInputEditCustomsqlOperation.value=apiCustomOpsReg.apimanager_editBtn;
			newInputEditCustomsqlOperation.name=operation.identification + "_Edit";
			newInputEditCustomsqlOperation.onclick = function() {
				ApiCustomOpsController.selectEditCustomOp(operation.identification);
			};
			//newCustomsqlParamDiv.appendChild(newInputEditCustomsqlOperation);
			OperationDivDesc.appendChild(newInputEditCustomsqlOperation);
				

        var newInputEliminarCustomsqlOperation = document.createElement('input');
			newInputEliminarCustomsqlOperation.id=operation.identification + "_Eliminar";
			newInputEliminarCustomsqlOperation.className="btn btn-sm red-sunglo";
			newInputEliminarCustomsqlOperation.style = "float: right;  margin-right: 4px;position: relative;top: -40px";
			newInputEliminarCustomsqlOperation.type="button";			
			newInputEliminarCustomsqlOperation.value=apiCustomOpsReg.apimanager_deleteBtn;
			newInputEliminarCustomsqlOperation.name=operation.identification + "_Eliminar";
			newInputEliminarCustomsqlOperation.onclick = function() {
				ApiCustomOpsController.removeCustomSqlOp(operation.identification);
			};
			//newCustomsqlParamDiv.appendChild(newInputEliminarCustomsqlOperation);
			OperationDivDesc.appendChild(newInputEliminarCustomsqlOperation);
		
       
        var newInputPathOperationCustomsql = document.createElement('span');
			newInputPathOperationCustomsql.id=operation.identification + "_PATH";			
			newInputPathOperationCustomsql.style = "padding-right:15px; min-width: 250px;display: inline-block";		
			newInputPathOperationCustomsql.innerHTML= '<span class="label label-success"><small>ENDPOINT</small></span> <span class="bold">' + operation.path + '</span>';
			newInputPathOperationCustomsql.name=operation.path + "_PATH";
        
		//newCustomsqlParamDiv.appendChild(newInputPathOperationCustomsql);
		OperationDivDesc.appendChild(newInputPathOperationCustomsql);
		
       
        for (var i = 0; i < operation.querystrings.length; i++) {
            if (operation.querystrings[i].name=="$query"){
                var newInputQueryOperationCustomsql = document.createElement('span');
                newInputQueryOperationCustomsql.id=operation.identification + "_QUERY";
                newInputQueryOperationCustomsql.style = "padding-right: 30px; min-width: 150px; display: inline-block";
                newInputQueryOperationCustomsql.innerHTML='<span class="label label-info "><small>QUERY</small></span> <span class="bold">' + operation.querystrings[i].value + "</span>";
                newInputQueryOperationCustomsql.name=operation.identification + "_QUERY";

                //newCustomsqlParamDiv.appendChild(newInputQueryOperationCustomsql);
				OperationDivDesc.appendChild(newInputQueryOperationCustomsql);
				
            }
        }

        var newInputDescOperationCustomsql = document.createElement('span');
        newInputDescOperationCustomsql.id=operation.identification + "_DESC";       
        newInputDescOperationCustomsql.style = "padding-left: 20px; display: inline-block";
		newInputDescOperationCustomsql.className = "text-truncate-lg";
        newInputDescOperationCustomsql.innerHTML=operation.description;
        newInputDescOperationCustomsql.name=operation.identification + "_DESC";

        //newCustomsqlParamDiv.appendChild(newInputDescOperationCustomsql);
		OperationDivDesc.appendChild(newInputDescOperationCustomsql);
		newCustomsqlParamDiv.appendChild(OperationDivDesc);

        customsqlOpsDiv.appendChild(newCustomsqlParamDiv);

        document.getElementById("divCUSTOMSQLS").style.display="block";
    }

    function loadCustomSql (op_name){
        if (op_name!=null && op_name!=""){
            var operation;
            for(var i=0; i<operations.length; i+=1){
                var op = operations [i];
                if (op.identification == op_name){
                	operation=op;
                }
            }
            $('#id_name_op_customsql').val(operation.identification);
            $('#id_desc_op_customsql').val(operation.description);

            for (var i = 0; i < operation.querystrings.length; i++) {
                if (operation.querystrings [i].name == "$query" ){
                	$('#id_query_op_customsql').val(operation.querystrings [i].value);
                	loadParamsFromQuery(operation.querystrings [i].value, op_name);
                }
            }

            loadParamsQueryValues(operation.querystrings);

            $('#id_name_op_customsql').prop('disabled', true);
        } else {
        	$('#id_name_op_customsql').val("");
        	$('#id_query_op_customsql').val("");
        	$('#id_desc_op_customsql').val("");
        			
        	loadParamsFromQuery("", "");

            $('#id_name_op_customsql').prop('disabled', false);

        }
        $('#dialog-customsql').modal('toggle');
    }


    function loadParamsQueryValues(querystrings){
        for (var i = 0; i < querystrings.length; i++) {
	       	 if (querystrings [i].name == "$query" ){
            } else if (querystrings [i].name == "$targetdb" ){
            	$('#id_customsql_targetBD').val(querystrings [i].value);
       	 	} else if (querystrings [i].name == "$formatResult" ){
       		 	$('#"id_customsql_formatresult').val(querystrings [i].value);
       	 	} else if (querystrings [i].name == "$queryType" ){
       		 	$('#id_customsql_querytype').val(querystrings [i].value);
       	 	} else {
       		 $('#customsqlParamType_' + querystrings [i].name).val(querystrings [i].value);
        	}
         }
    }

    function updateCustomSqlOperation(operation){
    	$('#' + operation.identification + "_PATH").html("<b>" + operation.path+ "</b>");

        for (var i = 0; i < operation.querystrings.length; i++) {
            if (operation.querystrings [i].name == "$query" ){
            	$('#' + operation.identification + '_QUERY').html(operation.querystrings [i].value);
            }
        }

        $('#' + operation.identification + "_DESC").html(operation.description);
    }

    function removeCustomOp(op_name){
        for(var i=0; i<operations.length; i+=1){
            var operation = operations [i];
            if (operation.identification == op_name){
            	operations.splice(i, 1);
            }
        }
        var operationsCustomSqlDiv=document.getElementById("divCUSTOMSQLS");
        var operationCustomSqlRemoveDiv = document.getElementById(op_name);
        operationsCustomSqlDiv.removeChild(operationCustomSqlRemoveDiv);

    }  
    
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return apiCustomOpsReg = Data;
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';	
			
		},
		
		// SELECTS EDIT OPERATION
		selectEditCustomOp: function(field) {
			logControl ? console.log(LIB_TITLE + ': selectEditCustomOp(field)') : '';
			selectEditOp(field);
		},
		
		// REMOVES EDIT OPERATION
		removeCustomSqlOp: function(field) {
			logControl ? console.log(LIB_TITLE + ': selectEditCustomOp(field)') : '';
			removeCustomOp(field);
		},
		
		// VALIDATE OP NAME
		validateName: function(field) {
			logControl ? console.log(LIB_TITLE + ': validateNameOp()') : '';
			validateNameOp(field);
		},
		
		// EXTRACT PARAMS
		loadParamsQuery: function(field, op_name){
			logControl ? console.log(LIB_TITLE + ': validateNameOp()') : '';
			loadParamsFromQuery(field, op_name);
		},
		
		// EXTRACT PARAMS
		loadParamsQueryType: function(){
			logControl ? console.log(LIB_TITLE + ': validateNameOp()') : '';
			loadParamQuerySQLType();
		},		
		
		// SAVE CHANGES
		saveCustom: function(){
			logControl ? console.log(LIB_TITLE + ': saveCustom()') : '';
			saveCustomsqlOperation();
		}
	};
}();
// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	ApiCustomOpsController.load(apiCustomOpsJson);
	// AUTO INIT CONTROLLER.
	ApiCustomOpsController.init();
});	

