var GadgetsTemplateCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Gadget Template Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	var myCodeMirror;
	// CONTROLLER PRIVATE FUNCTIONS	
	
	var navigateUrl = function(url){ window.location.href = url; }
	// DELETE GADGET
	var deleteGadgetTemplateConfirmation = function(gadgetTemplateId){
		console.log('deleteGadgetConfirmation() -> formId: '+ gadgetTemplateId);
		
		// no Id no fun!
		if ( !gadgetTemplateId ) {$.alert({title: 'ERROR!',type: 'red' , theme: 'dark', content: 'NO GATGET TEMPLATE SELECTED!'}); return false; }
		
		logControl ? console.log('deleteGadgetTemplateConfirmation() -> formAction: ' + $('.delete-gadget').attr('action') + ' ID: ' + $('.delete-gadget').attr('userId')) : '';
		
		// call user Confirm at header.
		HeaderController.showConfirmDialogGadgetTemplate('delete_gadget_template_form');	
	}
	
	// INIT CODEMIRROR
	var handleCodeMirror = function () {
		logControl ? console.log('handleCodeMirror() on -> templateCode') : '';	
		
        var myTextArea = document.getElementById('templateCode');
            myCodeMirror = CodeMirror.fromTextArea(myTextArea, {
        	mode: "code",
            lineNumbers: true,
            foldGutter: true,
            matchBrackets: true,
            styleActiveLine: true,
            theme:"material",         

        })
		myCodeMirror.setSize("100%", 350);
        myCodeMirror.on('change',editor => { var scope = angular.element(document.getElementsByTagName('livehtml')[0]).scope();
		  scope.$$childHead.vm.livecontent=editor.getValue();		  
          scope.$$childHead.vm.$onChanges([]);  
          searchProperties(editor.getValue());
         
        })    
        
        myCodeMirror.on("drop",function(editor,e,searchProperties) { 
        	var data = e.dataTransfer.getData("content");   
        	e.preventDefault();
        	myCodeMirror.replaceRange(dataFromId(data), CodeMirror.Pos(myCodeMirror.lastLine()));       
        });
        searchProperties(myCodeMirror.getValue());
        
    }
	
	
	// FORM VALIDATION
	var handleValidation = function() {
		logControl ? console.log('handleValidation() -> ') : '';
		// for more info visit the official plugin documentation:
		// http://docs.jquery.com/Plugins/Validation

		var form1 = $('#gadget_create_form');
		var error1 = $('.alert-danger');
		var success1 = $('.alert-success');

		// set current language
		currentLanguage = templateCreateReg.language || LANGUAGE;

		form1.validate({
					errorElement : 'span', // default input error message
											// container
					errorClass : 'help-block help-block-error', // default input
																// error message
																// class
					focusInvalid : false, // do not focus the last invalid
											// input
					ignore : ":hidden:not(.selectpicker)", // validate all
															// fields including
															// form hidden input
															// but not
															// selectpicker
					lang : currentLanguage,
					// custom messages
					messages : {

					},
					// validation rules
					rules : {
						identification : {
							minlength : 5,
							required : true
						},
						description : {
							minlength : 5,
							required : true
						},

					},
					invalidHandler : function(event, validator) { // display
																	// error
																	// alert on
																	// form
																	// submit
						success1.hide();
						error1.show();
						App.scrollTo(error1, -200);
					},
					errorPlacement : function(error, element) {
						if (element.is(':checkbox')) {
							error
									.insertAfter(element
											.closest(".md-checkbox-list, .md-checkbox-inline, .checkbox-list, .checkbox-inline"));
						} else if (element.is(':radio')) {
							error
									.insertAfter(element
											.closest(".md-radio-list, .md-radio-inline, .radio-list,.radio-inline"));
						} else {
							error.insertAfter(element);
						}
					},
					highlight : function(element) { // hightlight error inputs
						$(element).closest('.form-group').addClass('has-error');
					},
					unhighlight : function(element) { // revert the change
														// done by hightlight
						$(element).closest('.form-group').removeClass(
								'has-error');
					},
					success : function(label) {
						label.closest('.form-group').removeClass('has-error');
					},
					// ALL OK, THEN SUBMIT.
					submitHandler : function(form) {

						 success1.show();
			                error1.hide();
							form.submit();
					}
				});
	}
	
	
	
	function dataFromId(id){
		var ident = new Uint32Array(1);
		window.crypto.getRandomValues(ident);
		
		switch(id) {
	    case "label_text":	    
	        return '\n<!--label-s4c  name="parameterName-'+ident+'" type="text"-->';
	        break;
	    case "label_number":	    	
	    	  return '\n<!--label-s4c  name="parameterName-'+ident+'" type="number"-->';
	        break;
	    case "label_ds":	    	
	    	  return '\n<!--label-s4c  name="parameterName-'+ident+'" type="ds"-->';
	        break;	  
	    case "select_options":	    	
	   	  return '\n<!--select-s4c  name="parameterName-'+ident+'" type="ds" options="a,b,c" -->';	    	        
	      break;
	    default:
	        return "";
	}
		
	}
	

	
	
	function searchTag(regex,str){
		let m;
		let found=[];
		while ((m = regex.exec(str)) !== null) {  
		    if (m.index === regex.lastIndex) {
		        regex.lastIndex++;
		    }
		    m.forEach(function(item, index, arr){			
				found.push(arr[0]);			
			});  
		}
		return found;
	}
	
	function searchTagContentName(regex,str){
		let m;
		var content;
		while ((m = regex.exec(str)) !== null) {  
		    if (m.index === regex.lastIndex) {
		        regex.lastIndex++;
		    }
		    m.forEach(function(item, index, arr){			
		    	content = arr[0].match(/"([^"]+)"/)[1];			
			});  
		}
		return content;
	}
	
	function searchProperties(str){

		const regex =  /<![\-\-\s\w\>\=\"\'\,\:\+\_\/]*\>/g;
		const regexName = /name\s*=\s*\"[\s\w\>\=\-\'\+\_\/]*\s*\"/g;

		let found=[];
		found = searchTag(regex,str);		

		$('#parameters-form').empty();
		$('#parameters-form').append('<li class="list-group-item active">'+gadgetTemplateCreateJson.titleParametersSelected+'</li>');

		for (var i = 0; i < found.length; i++) {			
			var tag = found[i];
			if(tag.replace(/\s/g, '').search('type="text"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
		
				$('#parameters-form').append('<li class="list-group-item"><label class="bold">'+searchTagContentName(regexName,tag)+'&nbsp:&nbsp</label><label>'+gadgetTemplateCreateJson.parameterTextLabel+'</label></li>');
			}else if(tag.replace(/\s/g, '').search('type="number"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
			
				$('#parameters-form').append('<li class="list-group-item"><label class="bold">'+searchTagContentName(regexName,tag)+'&nbsp:&nbsp</label><label>'+gadgetTemplateCreateJson.parameterNumberLabel+'</label></li>');
			}else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('label-s4c')>=0){
				
				$('#parameters-form').append('<li class="list-group-item"><label class="bold">'+searchTagContentName(regexName,tag)+'&nbsp:&nbsp</label><label>'+gadgetTemplateCreateJson.parameterDsLabel+'</label></li>');
			}else if(tag.replace(/\s/g, '').search('type="ds"')>=0 && tag.replace(/\s/g, '').search('select-s4c')>=0){
			
				$('#parameters-form').append('<li class="list-group-item"><label class="bold">'+searchTagContentName(regexName,tag)+'&nbsp:&nbsp</label><label>'+gadgetTemplateCreateJson.parameterSelectLabel+'</label></li>');
			}
			
		} 	 
	
		}

	var updatePreview = function (){
		var scope = angular.element(document.getElementsByTagName('livehtml')[0]).scope();
		scope.$$childHead.vm.livecontent=$('#templateCode').val();
		
        scope.$$childHead.vm.$onChanges([]);  
		
	}
	
	 var drag = function (ev) {		 
		    ev.dataTransfer.setData("content", ev.target.id);
		}	
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return templateCreateReg = Data;
		},	
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';
			handleCodeMirror();
			handleValidation();
		},
		
		// REDIRECT
		go: function(url){
			logControl ? console.log(LIB_TITLE + ': go()') : '';	
			navigateUrl(url); 
		},
		updatePreview: function(){
			updatePreview();
		},
	
		// DELETE GADGET DATASOURCE 
		deleteGadgetTemplate: function(gadgetId){
			logControl ? console.log(LIB_TITLE + ': deleteGadget()') : '';	
			deleteGadgetTemplateConfirmation(gadgetId);			
		},
		drag: function(ev){
			drag(ev);
		}
		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	GadgetsTemplateCreateController.load(gadgetTemplateCreateJson);	
		
	// AUTO INIT CONTROLLER.
	GadgetsTemplateCreateController.init();
});
