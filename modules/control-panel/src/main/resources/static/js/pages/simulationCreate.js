var fields;


function generateJsonSimulationFields() {
	//return map
	var jsonMap = {};
	//Ontology fields
	var keys = Object.keys(fields);
	//For every field
	for (var i = 0; i < keys.length; i++) {
		var key = keys[i].replace(/\./g,"--");
		var inputs = $('#'+key+'Div input');
		var object = {};
		var functionSelected = $('#'+key).val();
		if(functionSelected == defaultOption) functionSelected = 'NULL';
		object['function'] = functionSelected;
		//For every input get value an add to object
		for (var j = 0; j < inputs.length; j++) {
			object[inputs.get(j).name] = inputs.get(j).value; 
		}

		jsonMap[keys[i]] = object;
	}
	$('#jsonMap').val(JSON.stringify(jsonMap));
}
function getTokensAndOntologies() {
		
	$("#ontologiesAndTokens").load('/controlpanel/devicesimulation/ontologiesandtokens', { 'clientPlatformId': $("#clientPlatforms").val()},getOntologyFields);
	$("#ontologiesAndTokens").show();
	$("#interval").show();
		
}
	
function getOntologyFields() {
	$("#ontologyFields").load('/controlpanel/devicesimulation/ontologyfields', { 'ontologyIdentification': $("#ontologies").val()});
	$("#ontologyFields").show();
}

function setFieldSimulator(field) {
	$("[name="+field+"]").val($("#simulator"+field).val());
}
function navigateUrl(url){  window.location.href = url;	}

	
function submitForm(formId) {
	generateJsonSimulationFields();
	$("#"+formId).submit()
}
function generateSimulatorFunctionDiv(field) {

	//Hidden div of the ontology Field
	var hiddenDiv= field+ 'Div';
	//Simulator function selected of the curren ontology field
	var functionSelected = $('#'+field).val();
	//If not function NULL
	if(functionSelected != 'NULL') {
		//html insert Auxiliar Div of the selected simulator function
		$('#'+hiddenDiv).html($('#'+functionSelected).html());
		//Assing unique ID to each input of the hiddenDiv
		var inputs = $('#'+hiddenDiv+' inputs');
		for (var i = 0; i < inputs.length; i++) {
			inputs.get(i).id = field + inputs.get(i).id;
		}
		//show
		$('#'+hiddenDiv).show();
	}else {
		//IF NULL THEN DELETE INNER HTML
		$('#'+hiddenDiv).html('');
	}

}
