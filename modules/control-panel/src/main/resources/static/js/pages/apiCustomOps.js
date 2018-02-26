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
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
    function openCustomSqlDialog(nombre_op) {
//    	checkformatresult();
//    	$("#customsql_paramaccordion").accordion("option", "active", 1);
//    	loadCustomSqlOperacion(nombre_op);
	
    	$( "#dialog-customsql" ).dialog({
    		resizable: false,
    		height:"auto",
    		width:800,
    		modal: true,
    		position: [($(window).width() / 2) - 340, 50],
    		dialogClass: 'DeleteConfirmDialog',
    		buttons: {
    			"GUARDAR": function() {
    				guardarOperacionCustomsql(this, nombre_op);
    			},
    			"CANCELAR": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    }
    
    function loadParamQuerySQLType() {
   	 var query = document.getElementById("id_query_op_customsql").value;
        var error = "";
        error = isValidQuery(query);
        if (error==""){
       	    clearParams();
       	    mostrarParametros(query);
        } else {
       	    document.getElementById("dialog-error").innerHTML=error;
       	    showErrorDialog();
        }
    }

    function loadParamsQuery(campo, nombre_op) {
        clearParams();
        if (nombre_op==null || nombre_op==""){
            var error = "";
            if (campo != null && campo !=""){
                error = isValidQuery(campo);
            }
            if (error==""){
                mostrarParametros(campo);
            } else {
                document.getElementById("dialog-error").innerHTML=error;
                showErrorDialog();
            }
        } else {
            mostrarParametros(campo);
        }
    }

//    function isValidQuery(campo){
//        if (campo != null && campo !=""){
//            if (((campo.toUpperCase().indexOf("SELECT")>=0)&&(document.getElementById("id_customsql_querytype").value=="SQLLIKE"))||
//                ((campo.toUpperCase().indexOf("DB.")>=0)&&(document.getElementById("id_customsql_querytype").value=="NATIVE"))){
//                if (campo.indexOf($("#id_campo_ontologia option:selected").text())>=0){
//               	 if (((campo.split("{$").length - 1)==(campo.split("}").length - 1) || (document.getElementById("id_customsql_querytype").value=="NATIVE"))){
//                          return "";
//                    } else {
//                          return ([[#{apimanager_customsql_error_query_params}]]);
//                    }
//                } else {
//                       return ([[#{apimanager_customsql_error_query_ontology}]]);
//                }
//            } else {
//                   return ([[#{apimanager_customsql_error_query}]]);
//            }
//        } else {
//            return ([[#{apimanager_customsql_error_required}]]);
//        }
//    }

    function clearParams() {
   	 document.getElementById("customsql_paramsquery").innerHTML = '';
   	 document.getElementById("customsql_params_div").style.display="none";
   	 document.getElementById("customsql_noparam_div").style.display="block";
    }

    function mostrarParametros(query) {
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
   		 document.getElementById("customsql_params_div").style.display="block";
   		 document.getElementById("customsql_noparam_div").style.display="none";
   	 } else {
   		 document.getElementById("customsql_params_div").style.display="none";
   		 document.getElementById("customsql_noparam_div").style.display="block";
   	 }

    }


    function loadParamQuery(param) {
        var customsqlParamaDiv=document.getElementById("customsql_paramsquery");

        var newCustomsqlParamDiv = document.createElement('div');
        newCustomsqlParamDiv.id= "customsql_param_" + param;

        var newCustomsqlParamFieldSet = document.createElement('fieldset');
        newCustomsqlParamFieldSet.id = "customsql_param_fieldset" + param;

        newCustomsqlParamFieldSet.style.width="40%";
        newCustomsqlParamFieldSet.style.margin="10px";
        newCustomsqlParamFieldSet.style.marginTop="10px";
        newCustomsqlParamFieldSet.style.padding="10px";
        newCustomsqlParamFieldSet.style.border="1px #d0d2d9 dotted";
        newCustomsqlParamFieldSet.style.display="inline";

        var newLabelCustomsqlParam = document.createElement('label');
        newLabelCustomsqlParam.id = param;
        newLabelCustomsqlParam.className="description";
        newLabelCustomsqlParam.style.marginLeft="20px";
        newLabelCustomsqlParam.innerHTML=param;

        newCustomsqlParamFieldSet.appendChild(newLabelCustomsqlParam);

        var newInputCustomsqlParam = document.createElement('select');
        newInputCustomsqlParam.id="customsqlParamType_" + param;
        newInputCustomsqlParam.style.cssFloat="right";

        var optionString = document.createElement( 'option' );
        optionString.value = optionString.text = "STRING"
        newInputCustomsqlParam.add(optionString);
        var optionNumber = document.createElement( 'option' );
        optionNumber.value = optionNumber.text = "NUMBER"
        newInputCustomsqlParam.add(optionNumber);
        var optionBoolean = document.createElement( 'option' );
        optionBoolean.value = optionBoolean.text = "BOOLEAN"
        newInputCustomsqlParam.add(optionBoolean);
        var optionDate = document.createElement( 'option' );
        optionDate.value = optionDate.text = "DATE"
        newInputCustomsqlParam.add(optionDate);

        newCustomsqlParamFieldSet.appendChild(newInputCustomsqlParam);

        customsqlParamaDiv.appendChild(newCustomsqlParamFieldSet);

        var parameter = {nombre: param, condicion: "REQUIRED", tipo: document.getElementById("customsqlParamType_" + param).value, valor: document.getElementById("customsqlParamType_" + param).value  ,descripcion: ""};
        customsql_queryparam.push(parameter);
    }

//    function guardarOperacionCustomsql(dialog, nombre_op_editar_customsql){
//        var tipo_op_customsql = document.getElementById("id_tipo_op_customsql").value;
//        var nombre_op_customsql = document.getElementById("id_nombre_op_customsql").value;
//        var errorQuery = isValidQuery(document.getElementById("id_query_op_customsql").value);
//
//        var desc_op_customsql = document.getElementById("id_desc_op_customsql").value;
//        if (tipo_op_customsql!=null && tipo_op_customsql!="" && nombre_op_customsql!=null && nombre_op_customsql!="" && desc_op_customsql!=null && desc_op_customsql!=""){
//       	 if (errorQuery!=null && errorQuery==""){
//	             if (nombre_op_editar_customsql==null || nombre_op_editar_customsql==""){
//	                 if (!existeOperacion(nombre_op_customsql)){
//	                     var querystrings = new Array();
//	                     var headers = new Array();
//	                     var operacion = {identificacion: nombre_op_customsql, descripcion: desc_op_customsql , operacion: tipo_op_customsql, endpoint: "", path: "", querystrings: querystrings, headers: headers};
//
//	                     guardarScriptCustomsql(operacion);
//	                     guardarParamQueryCustomsql(operacion);
//
//	                     addOperacionCustomsql(operacion);
//
//
//	                     operaciones.push(operacion);
//
//	                     $( dialog ).dialog( "close" );
//	                 } else {
//	                     document.getElementById("dialog-error").innerHTML=[[#{apimanager_error_operacion_existe}]];
//	                     showErrorDialog();
//	                 }
//	             } else {
//	                 for(var i=0; i<operaciones.length; i+=1){
//	                     if (operaciones [i].identificacion == nombre_op_editar_customsql){
//	                         operaciones [i].descripcion=desc_op_customsql;
//	                         operaciones [i].operacion=tipo_op_customsql;
//
//	                         operaciones [i].querystrings = new Array();
//	                         guardarScriptCustomsql(operaciones [i]);
//
//	                         guardarParamQueryCustomsql(operaciones [i]);
//	                         break;
//	                     }
//	                 }
//	                 updateCustomSqlOperacion(operaciones[i]);
//	                 $( dialog ).dialog( "close" );
//	             }
//	         } else {
//	             document.getElementById("dialog-error").innerHTML=errorQuery;
//	             showErrorDialog();
//	         }
//        } else {
//            document.getElementById("dialog-error").innerHTML=[[#{apimanager_error_campos}]];
//            showErrorDialog();
//        }
//    }

    function guardarScriptCustomsql(operacion){
   	 var condicion = editors[0].getValue().trim();
   	 var ifthen = editors[1].getValue().trim();

   	 if (condicion != null && condicion != "" && ifthen != null && ifthen != ""){
   		 var script = {condicion: condicion, ifthen: ifthen};
   	     operacion.script=script;
   	 } else {
   		  if(operacion.script){
   			  delete operacion["script"];
   		  }
   	 }
    }

    function guardarParamQueryCustomsql(operacion){
   	 var queryParameter = {nombre: "$query", condicion: "CONSTANT", tipo: "STRING", valor: document.getElementById("id_query_op_customsql").value , descripcion: ""};
   	 operacion.querystrings.push(queryParameter);
        var targetBDParameter = {nombre: "$targetdb", condicion: "CONSTANT", tipo: "STRING", valor: document.getElementById("id_customsql_targetBD").value , descripcion: ""};
        operacion.querystrings.push(targetBDParameter);
        var formatresultParameter = {nombre: "$formatResult", condicion: "CONSTANT", tipo: "STRING", valor: document.getElementById("id_customsql_formatresult").value , descripcion: ""};
        operacion.querystrings.push(formatresultParameter);
        var querytypeBDParameter = {nombre: "$queryType", condicion: "CONSTANT", tipo: "STRING", valor: document.getElementById("id_customsql_querytype").value , descripcion: ""};
        operacion.querystrings.push(querytypeBDParameter);
        var path = "\\" + operacion.identificacion;
        if (customsql_queryparam.length>0){
       	 path=path + "?";
        }
        for (var i = 0; i < customsql_queryparam.length; i++) {
       	customsql_queryparam [i].valor = document.getElementById("customsqlParamType_" + customsql_queryparam [i].nombre).value;
       	customsql_queryparam [i].tipo = document.getElementById("customsqlParamType_" + customsql_queryparam [i].nombre).value;
       	operacion.querystrings.push(customsql_queryparam [i]);
       	path = path + "$" + customsql_queryparam [i].nombre + "={" + customsql_queryparam [i].nombre +"}";
       	if (i < customsql_queryparam.length-1){
       		path = path + "&";
       	}
        }
        operacion.path = path;
    }


//    function addOperacionCustomsql(operacion){
//   	 var customsqlOpsDiv=document.getElementById("divCUSTOMSQLS");
//
//        var newCustomsqlParamDiv = document.createElement('div');
//        newCustomsqlParamDiv.id= operacion.identificacion;
//        newCustomsqlParamDiv.className= "op_div_selected";
//
//        var newInputCustomsqlOperacionDiv = document.createElement('div');
//        newInputCustomsqlOperacionDiv.className= "op_button_div";
//        newInputCustomsqlOperacionDiv.style.marginTop="30px";
//        newInputCustomsqlOperacionDiv.style.marginBottom="30px";
//
//        var newInputCustomsqlOperacion = document.createElement('input');
//        newInputCustomsqlOperacion.id=operacion.identificacion + "_OPERATION";
//        newInputCustomsqlOperacion.className="op_button_selected";
//        newInputCustomsqlOperacion.style.width="110px";
//        newInputCustomsqlOperacion.style.marginLeft="25px";
//        newInputCustomsqlOperacion.type="reset";
//        newInputCustomsqlOperacion.value="CUSTOM (query)";
//        newInputCustomsqlOperacion.name="CUSTOM_SQL";
//        newInputCustomsqlOperacion.disabled="disabled";
//
//        newInputCustomsqlOperacionDiv.appendChild(newInputCustomsqlOperacion);
//        newCustomsqlParamDiv.appendChild(newInputCustomsqlOperacionDiv);
//
//        var newLabelCustomsqlOperacion = document.createElement('label');
//        newLabelCustomsqlOperacion.id=operacion.identificacion + "_LABEL";
//        newLabelCustomsqlOperacion.className="description";
//        newLabelCustomsqlOperacion.style.paddingLeft="20px";
//        newLabelCustomsqlOperacion.innerHTML=operacion.identificacion;
//
//        newCustomsqlParamDiv.appendChild(newLabelCustomsqlOperacion);
//
//        var newInputEditCustomsqlOperacion = document.createElement('input');
//        newInputEditCustomsqlOperacion.id=operacion.identificacion + "_Edit";
//        newInputEditCustomsqlOperacion.className="button_text";
//        newInputEditCustomsqlOperacion.style.cssFloat="right";
//        newInputEditCustomsqlOperacion.type="button";
//        newInputEditCustomsqlOperacion.style.marginTop="30px";
//        newInputEditCustomsqlOperacion.value=[[#{apimanager_autenticacion_editar}]];
//        newInputEditCustomsqlOperacion.name=operacion.identificacion + "_Edit";
//        newInputEditCustomsqlOperacion.onclick = function() {
//       	 showCustomSqlDialog(operacion.identificacion);
//        };
//
//        newCustomsqlParamDiv.appendChild(newInputEditCustomsqlOperacion);
//
//        var newInputEliminarCustomsqlOperacion = document.createElement('input');
//        newInputEliminarCustomsqlOperacion.id=operacion.identificacion + "_Eliminar";
//        newInputEliminarCustomsqlOperacion.className="button_text";
//        newInputEliminarCustomsqlOperacion.style.cssFloat="right";
//        newInputEliminarCustomsqlOperacion.type="button";
//        newInputEliminarCustomsqlOperacion.style.marginTop="30px";
//        newInputEliminarCustomsqlOperacion.value=[[#{apimanager_autenticacion_eliminar}]];
//        newInputEliminarCustomsqlOperacion.name=operacion.identificacion + "_Eliminar";
//        newInputEliminarCustomsqlOperacion.onclick = function() {
//       	 removeCustomSqlOperacion(operacion.identificacion);
//        };
//
//        newCustomsqlParamDiv.appendChild(newInputEliminarCustomsqlOperacion);
//
//        newCustomsqlParamDiv.appendChild(document.createElement('br'));
//        newCustomsqlParamDiv.appendChild(document.createElement('br'));
//
//        var newInputPathOperacionCustomsql = document.createElement('span');
//        newInputPathOperacionCustomsql.id=operacion.identificacion + "_PATH";
//        newInputPathOperacionCustomsql.className="element text large";
//        newInputPathOperacionCustomsql.style.paddingLeft="20px";
//        newInputPathOperacionCustomsql.style.width="93%";
//        newInputPathOperacionCustomsql.innerHTML="<b>" + operacion.path + "</b>";
//        newInputPathOperacionCustomsql.name=operacion.path + "_PATH";
//
//        newCustomsqlParamDiv.appendChild(newInputPathOperacionCustomsql);
//
//        newCustomsqlParamDiv.appendChild(document.createElement('br'));
//        newCustomsqlParamDiv.appendChild(document.createElement('br'));
//
//        for (var i = 0; i < operacion.querystrings.length; i++) {
//            if (operacion.querystrings[i].nombre=="$query"){
//                var newInputQueryOperacionCustomsql = document.createElement('span');
//                newInputQueryOperacionCustomsql.id=operacion.identificacion + "_QUERY";
//                newInputQueryOperacionCustomsql.className="element text large";
//                newInputQueryOperacionCustomsql.style.paddingLeft="20px";
//                newInputQueryOperacionCustomsql.style.width="93%";
//                newInputQueryOperacionCustomsql.innerHTML="<b>" + operacion.querystrings[i].valor + "</b>";
//                newInputQueryOperacionCustomsql.name=operacion.identificacion + "_QUERY";
//
//                newCustomsqlParamDiv.appendChild(newInputQueryOperacionCustomsql);
//            }
//        }
//
//        newCustomsqlParamDiv.appendChild(document.createElement('br'));
//
//        var newInputDescOperacionCustomsql = document.createElement('span');
//        newInputDescOperacionCustomsql.id=operacion.identificacion + "_DESC";
//        newInputDescOperacionCustomsql.className="element text large";
//        newInputDescOperacionCustomsql.style.paddingLeft="20px";
//        newInputDescOperacionCustomsql.innerHTML=operacion.descripcion;
//        newInputDescOperacionCustomsql.name=operacion.identificacion + "_DESC";
//
//        newCustomsqlParamDiv.appendChild(newInputDescOperacionCustomsql);
//
//        customsqlOpsDiv.appendChild(newCustomsqlParamDiv);
//
//        document.getElementById("divCUSTOMSQLS").style.display="block";
//    }

    function loadCustomSqlOperacion(nombre_op){
        if (nombre_op!=null && nombre_op!=""){
            var operacion;
            for(var i=0; i<operaciones.length; i+=1){
                var op = operaciones [i];
                if (op.identificacion == nombre_op){
                    operacion=op;
                }
            }
            document.getElementById("id_nombre_op_customsql").value=operacion.identificacion;
            document.getElementById("id_desc_op_customsql").value=operacion.descripcion;
            if (operacion.script){
           	 document.getElementById("ifTextarea").value=operacion.script.condicion;
           	 document.getElementById("thenTextarea").value=operacion.script.ifthen;
            } else {
           	 document.getElementById("ifTextarea").value="";
                document.getElementById("thenTextarea").value="";

            }

            for (var i = 0; i < operacion.querystrings.length; i++) {
                if (operacion.querystrings [i].nombre == "$query" ){
                    document.getElementById("id_query_op_customsql").value = operacion.querystrings [i].valor;
                    loadParamsQuery(operacion.querystrings [i].valor, nombre_op);
                }
            }

            loadParamsQueryValues(operacion.querystrings);

            document.getElementById("id_nombre_op_customsql").disabled=true;
        } else {
            document.getElementById("id_nombre_op_customsql").value="";
            document.getElementById("id_query_op_customsql").value="";
            document.getElementById("id_desc_op_customsql").value="";
            document.getElementById("ifTextarea").value="";
            document.getElementById("thenTextarea").value="";
            loadParamsQuery("", "");

            document.getElementById("id_nombre_op_customsql").disabled=false;

        }
        loadPostProcesado();
    }


    function loadParamsQueryValues(querystrings){
        for (var i = 0; i < querystrings.length; i++) {
       	 if (querystrings [i].nombre == "$query" ){
            } else if (querystrings [i].nombre == "$targetdb" ){
       		 document.getElementById("id_customsql_targetBD").value = querystrings [i].valor;
       	 } else if (querystrings [i].nombre == "$formatResult" ){
       		 document.getElementById("id_customsql_formatresult").value = querystrings [i].valor;
       	 } else if (querystrings [i].nombre == "$queryType" ){
       		 document.getElementById("id_customsql_querytype").value = querystrings [i].valor;
       	 } else {
       		 document.getElementById("customsqlParamType_" + querystrings [i].nombre).value = querystrings [i].valor;
       	 }
         }
    }

    function updateCustomSqlOperacion(operacion){
        document.getElementById(operacion.identificacion + "_PATH").innerHTML="<b>" + operacion.path+ "</b>";

        for (var i = 0; i < operacion.querystrings.length; i++) {
            if (operacion.querystrings [i].nombre == "$query" ){
              document.getElementById(operacion.identificacion + "_QUERY").innerHTML=operacion.querystrings [i].valor;
            }
        }

        document.getElementById(operacion.identificacion + "_DESC").innerHTML=operacion.descripcion;
    }

    function removeCustomSqlOperacion(nombre_op){
        for(var i=0; i<operaciones.length; i+=1){
            var operacion = operaciones [i];
            if (operacion.identificacion == nombre_op){
                operaciones.splice(i, 1);
            }
        }
        var operacionesCustomSqlDiv=document.getElementById("divCUSTOMSQLS");
        var operacionCustomSqlEliminarDiv = document.getElementById(nombre_op);
        operacionesCustomSqlDiv.removeChild(operacionCustomSqlEliminarDiv);

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
		
		// OPEN DIALOG
		showCustomSqlDialog: function(nombre_op) {
			logControl ? console.log(LIB_TITLE + ': changeCacheTimeout()') : '';
			openCustomSqlDialog(nombre_op);
		},
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	ApiCustomOpsController.load(apiCustomOpsJson);	
		
	// AUTO INIT CONTROLLER.
	ApiCustomOpsController.init();
});
