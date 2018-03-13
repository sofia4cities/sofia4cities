var HeaderController = function() {

	// DEFAULT PARAMETERS, VAR, CONSTS. 
	var APPNAME = 'Smart4Cities Control Panel'; 
	var LIB_TITLE = 'Header Controller';	
	var logControl = 0;     

	// CONTROLLER PRIVATE FUNCTIONS

	// GENERIC HEADER SEARCH
	var searchDocs = function(){		
		logControl ? console.log('searchDocs() Search --> '+ $("#search-query").val()) : '';

		// NOT-AVAILABLE 
		$.alert({title: 'Sofia4Cities Search:', type: 'red' ,content: 'FUNCTIONALITY NOT-AVAILABLE!'}); return false;

		var search = $("#search-query").val();
		var url = "/console/api/rest/searchDocs/"+search;
		var settings = {"async": true, "url": url, "method": "GET", "headers": {"cache-control": "no-cache"} };

		// llamada para la búsqueda
		$.ajax(settings).done(function (response) {
			hideDocPost();
			if( !Array.isArray(response) ){
				showErrorDialog();
				return;
			}

			// total resultados obtenidos.
			$("#docs-count").text(response.length);

			blogResults = response.filter(function(f){ return f.type=="blog";});
			docsResults = response.filter(function(f){ return f.type=="doc";});

			// HTML de salida.
			var html = "";
			// BLOGS
			if( blogResults.length > 0 ){
				$("#blog-content-title").show();
				for ( var i = 0; i < blogResults.length; i++){
					var doc = blogResults[i];
					categorias = doc.categoria.join(" ");
					// TO-DO: ajustar css.
					html += "<div style='padding: 2px;margin-bottom: 10px;padding-bottom: 10px;' class='col-md-4 col-lg-3 "+ categorias.toLowerCase() +"'>"
					+ "<div class='search-card'>"
					+ "<div class='search-card-title'>"
					+ "<a onClick='javascript:showDocPost(\""+ doc.link +"\")'><span class='glyphicon glyphicon-blog'></span>"+ doc.title +"</a>"
					+ "</div>"
					+ "<div class='search-card-body'>"
					if (doc.imageUrl) { html +="<img style='width: 100%;;' src="+ doc.imageUrl +"></img>"}; 

					html += "<p>"+ doc.content +"</p>"
					+ "</div>"
					+ "<div class='search-card-foot'>"
					+ "<span class='glyphicon glyphicon-time'></span>"+new Date(doc.date).toLocaleDateString()+""
					+ "<span class='pull-right glyphicon glyphicon-new-window' onClick='javascript:window.open(\""+ doc.link +"\", \"_blank\")'></span>"
					+ "</div></div>"
					+ "</div>"
				}
			}
			else{
				// NO BLOGS
				$("#blog-content-title").hide();
			}

			// ADD HTML RESULT. 		
			$('#blog-content').html(html);

			// DOCS.
			html = "<ul class='searchdoc'>";
			if( docsResults.length > 0){
				$("#docs-content-title").show();
				for (var i = 0; i < docsResults.length; i++){
					var doc = docsResults[i];
					categorias = doc.categoria.join(" ");
					// TO-DO: ajustar css.
					html += "<li class='"+categorias.toLowerCase()+"'>"
					+ "<a onClick='javascript:showDocPost(\""+ doc.link +"\")'><span class='glyphicon glyphicon-book'></span> "+ doc.title +"</a>"
					+ "<br><span>"+ doc.content +"</span>"
					+ "</li>";
				}
			}
			else{
				// NO DOCS
				$("#docs-content-title").hide();
			}

			// ADD HTML RESULT.
			html += "</ul>";
			$('#docs-content').html(html);
			$('#modalDocs').modal();
			$(".modal-backdrop").hide()
		});
	}

	// SHOW SEARCH DOCS
	var showDocPost = function(url){		
		logControl ? console.log('showDocPost()...') : '';

		$("#result-show-content").html("<iframe id='map-iframe' width='100%' height='100%' frameborder=0 scrolling=no" + "marginheight=0 marginwidth=0 src='" + url +"'></iframe>");
		$("#modalDocs-result-show").show();
		$("#btn-search-back").show();
		$("#modalDocs-content").hide();		
	}

	// HIDE SEARCH DOCS
	var hideDocPost = function(){
		logControl ? console.log('hideDocPost()...') : '';

		$("#modalDocs-result-show").hide();
		$("#btn-search-back").hide();
		$("#modalDocs-content").show();				
	}

	// GENERIC-CONFIRM-DIALOG
	var showConfirmDialog = function(formId){

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var	Content = headerReg.genericConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// CONFIG-CONFIRM-DIALOG
	var showConfigurationConfirmDialog = function(formId){

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var	Content = headerReg.configurationConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// CONFIG-CONFIRM-DIALOG
	var showScheduledSearchConfirmDialog = function(formId){

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var	Content = headerReg.scheduledSearchConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// TWITTERLISTENING-CONFIRM-DIALOG
	var showTwitterListeningConfirmDialog = function(formId){		
		logControl ? console.log('showConfirmDialogTwitterlistening()...') : '';

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var Content = headerReg.twitterListeningConfirm;
		var Title = headerReg.titleConfirm + ':';		

		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: Title,
			theme: 'dark',
			type: 'red',
			columnClass: 'medium',
			content: Content,
			draggable: true,
			dragWindowGap: 100,
			backgroundDismiss: true,
			closeIcon: true,
			buttons: {
				remove: {
					text: Remove,
					btnClass: 'btn-sm btn-danger btn-outline',
					action: function(){ 
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// ONTOLOGY-CONFIRM-DIALOG
	var showConfirmDialogOntologia = function(formId){		
		logControl ? console.log('showConfirmDialogOntologia()...') : '';

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var Content = headerReg.ontologyConfirm;
		var Title = headerReg.titleConfirm + ':';		

		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: Title,
			theme: 'dark',
			type: 'red',
			columnClass: 'medium',
			content: Content,
			draggable: true,
			dragWindowGap: 100,
			backgroundDismiss: true,
			closeIcon: true,
			buttons: {
				remove: {
					text: Remove,
					btnClass: 'btn-sm btn-danger btn-outline',
					action: function(){ 
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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
	
	// ONTOLOGY-CONFIRM-DIALOG
	var showConfirmDialogDigitalTwinType = function(formId){		
		logControl ? console.log('showConfirmDialogDigitalTwinType()...') : '';

		// i18 labels
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;
		var Content = headerReg.digitalTwinTypeConfirm;
		var Title = headerReg.titleConfirm + ':';		

		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: Title,
			theme: 'dark',
			type: 'red',
			columnClass: 'medium',
			content: Content,
			draggable: true,
			dragWindowGap: 100,
			backgroundDismiss: true,
			closeIcon: true,
			buttons: {
				remove: {
					text: Remove,
					btnClass: 'btn-sm btn-danger btn-outline',
					action: function(){ 
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// USER-CONFIRM-DIALOG
	var showConfirmDialogUsuario = function(formId){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.userConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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
	
	// DATASOURCE-CONFIRM-DIALOG
	var showConfirmDialogDatasource = function(formId){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.gadgetDatasourceConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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
	
	// GADGET-CONFIRM-DIALOG
	var showConfirmDialogGadget = function(formId){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.gadgetConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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
	
	// DASHBOARDS-CONFIRM-DIALOG
	var showConfirmDialogDashboard = function(formId){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.dashboardConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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

	// DEVICE-CONFIRM-DIALOG
	var showConfirmDialogDevice = function(formId){	

		//i18 labels
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		var Content = headerReg.deviceConfirm;
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
						if ( document.forms[formId] ) { document.forms[formId].submit(); } else { $.alert({title: 'ERROR!',content: 'NO FORM SELECTED!'}); }
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
	
	// SERVER ERRORS-DIALOG
	var messages = function(){		
		var Close = headerReg.btnCancelar;
		var infoTitle = headerReg.informationtitle;
		if ( headerReg.messages !== null ){			
			// jquery-confirm DIALOG SYSTEM.
			$.confirm({
				icon: 'fa fa-info-circle',
				title: infoTitle + ':',
				theme: 'dark',
				type: 'blue',
				content: headerReg.messages,
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
		} else { logControl ? console.log('|---> messages() -> NO MESSAGES FROM SERVER.') : ''; }		
	}

	// SERVER INFORMATION-DIALOG (ERRORS)
	var information = function(){		
		var Close = headerReg.btnCancelar;

		if (headerReg.informacion !== null ){			
			// jquery-confirm DIALOG SYSTEM.
			$.confirm({
				icon: 'fa fa-info-circle',
				title: 'INFO',
				theme: 'dark',
				content: headerReg.informacion,
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
		else { logControl ? console.log('|---> information() -> NO ERROR INFO.') : ''; }		
	}	


	// CONTROLLER PUBLIC FUNCTIONS 
	return{

		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER.
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return headerReg = Data;
		},

		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';

			// CALL MESSAGES/ERRORS
			messages();			
			// CALL INFO
			information();
		},

		// SEARCH
		search: function(){
			logControl ? console.log(LIB_TITLE + ': search()') : '';
			searchDocs();			
		},

		// SERVER-ERROR CONTROL-DIALOG
		showErrorDialog: function(){		
			logControl ? console.log('showErrorDialog()...') : '';
			var Close = headerReg.btnCancelar;

			// jquery-confirm DIALOG SYSTEM.
			$.confirm({
				icon: 'fa fa-bug',
				title: 'ERROR',
				theme: 'dark',
				content: 'to-do',
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
		},

		// GENERIC-CONFIRM-DIALOG
		showConfirmDialog : function(formId){		
			logControl ? console.log('showConfirmDialog()...') : '';
			showConfirmDialog(formId);
		},

		// ONTOLOGY-CONFIRM-DIALOG
		showConfirmDialogOntologia : function(formId){		
			logControl ? console.log('showConfirmDialogOntologia()...') : '';
			showConfirmDialogOntologia(formId);
		},
		// DIGITALTWINTYPE-CONFIRM-DIALOG
		showConfirmDialogDigitalTwinType : function(formId){		
			logControl ? console.log('showConfirmDialogDigitalTwinType()...') : '';
			showConfirmDialogDigitalTwinType(formId);
		},
		showTwitterListeningConfirmDialog: function(formId){		
			logControl ? console.log('showTwitterListeningConfirmDialog()...') : '';
			showTwitterListeningConfirmDialog(formId);
		},
		// CONFIGURATION-CONFIRM-DIALOG
		showConfigurationConfirmDialog : function(formId){		
			logControl ? console.log('showConfigurationConfirmDialog()...') : '';
			showConfigurationConfirmDialog(formId);
		},
		// SCHEDULEDSEARCH-CONFIRM-DIALOG
		showScheduledSearchConfirmDialog : function(formId){		
			logControl ? console.log('showScheduledSearchConfirmDialog()...') : '';
			showScheduledSearchConfirmDialog(formId);
		},

		// USER-CONFIRM-DIALOG
		showConfirmDialogUsuario : function(formId){		
			logControl ? console.log('showConfirmDialogUsuario()...') : '';
			showConfirmDialogUsuario(formId);
		},
		
		// DATASOURCE-CONFIRM-DIALOG
		showConfirmDialogDatasource : function(formId){		
			logControl ? console.log('showConfirmDialogDatasource()...') : '';
			showConfirmDialogDatasource(formId);
		},
		
		// DATASOURCE-CONFIRM-DIALOG
		showConfirmDialogDashboard : function(formId){		
			logControl ? console.log('showConfirmDialogDashboard()...') : '';
			showConfirmDialogDashboard(formId);
		},
		
		// GADGET-CONFIRM-DIALOG
		showConfirmDialogGadget : function(formId){		
			logControl ? console.log('showConfirmDialogDashboard()...') : '';
			showConfirmDialogDashboard(formId);
		},
			
		showConfirmDialogDevice: function(formId){		
			logControl ? console.log('showConfirmDialogDevice()...') : '';
			showConfirmDialogDevice(formId);
		},
		
	};
}();

//AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {

	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	HeaderController.load(headerJson);

	// AUTO INIT CONTROLLER.
	HeaderController.init();
});
