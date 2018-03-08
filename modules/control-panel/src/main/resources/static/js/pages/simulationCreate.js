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
	$("#ontologyFields").load('/controlpanel/devicesimulation/ontologyfields', { 'ontologyIdentification': $("#ontologies").val()}, function(data){editFieldParameters()});
	$("#ontologyFields").show();
}

function editFieldParameters() {
	
	if(simulationJson != null)
	{
		var keys = Object.keys(fields);
		for (var i = 0; i < keys.length; i++) {
			var key = keys[i].replace(/\./g,"--");
			var hiddenDiv= key+ 'Div';
			$('#'+key).val(simulationJson['fields'][keys[i]]['function']);
			var functionSelected = $('#'+key).val();
			$('#'+hiddenDiv).html($('#'+functionSelected).html());;
			var inputs = $('#'+key+'Div input');

			for (var j = 0; j < inputs.length; j++) {
				inputs.get(j).value = simulationJson['fields'][keys[i]][inputs.get(j).name];
			}

			$('#'+hiddenDiv).show();
			
		}
	}
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

var  deleteSimulation= function (id){
		console.log('deleteSimulationConfirmation() -> id: '+ id);
		
		// no Id no fun!
		if ( !id ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO SIMULATION SELECTED!'}); return false; }
		
		// call  Confirm 
		showConfirmDeleteDialog(id);	
	} 
	
	
	var showConfirmDeleteDialog = function(id){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.deviceSimulationConfirm;
		var Title = headerReg.titleConfirm + ':';

		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: Title,
			theme: 'dark',
			columnClass: 'medium',
			content: Content,
			draggable: true,
			dragWindowGap: 100,
			backgroundDismiss: true,
			closeIcon: true,
			buttons: {
				remove: {
					text: Remove,
					btnClass: 'btn btn-sm btn-danger btn-outline',
					action: function(){ 
						console.log(id);
						$.ajax({
						    url: '/controlpanel/devicesimulation/'+id,
						    type: 'DELETE',						  
						    success: function(result) {
						    	if(result == 'ok') {navigateUrl('/controlpanel/devicesimulation/list');}
						    }
						});
					}											
				},
				close: {
					text: Close,
					btnClass: 'btn btn-sm btn-default btn-outline',
					action: function (){} //GENERIC CLOSE.		
				}
			}
		});
	}	