var HeaderController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Smart4Cities Control Panel'; 
	var LIB_TITLE = 'Header Controller';	
    var logControl = 1;
    var alertContainer = '.alert-zone';    
	
	// PRIVATE FUNCTIONS 	
	
	// BUSCADOR DE BLOGS Y DOCUMENTOS DEL HEADER Y MENU.
	var searchDocs = function(){		
		logControl ? console.log('searchDocs() Buscar --> '+ $("#search-query").val()) : '';
		
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
				// NO HAY BLOGS
				$("#blog-content-title").hide();
			}
		
			// AGREGAMOS EL HTML RESULTANTE. 		
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
				// NO HAY DOCS
				$("#docs-content-title").hide();
			}
			
			// AGREGAMOS EL HTML RESULTANTE.
			html += "</ul>";
			$('#docs-content').html(html);
			$('#modalDocs').modal();
			$(".modal-backdrop").hide()
		});
	}
		
	// CREA UN IFRAME PARA LOS DOCS.
	var showDocPost = function(url){		
		logControl ? console.log('showDocPost()...') : '';
		
		$("#result-show-content").html("<iframe id='map-iframe' width='100%' height='100%' frameborder=0 scrolling=no" + "marginheight=0 marginwidth=0 src='" + url +"'></iframe>");
		$("#modalDocs-result-show").show();
		$("#btn-search-back").show();
		$("#modalDocs-content").hide();		
	}
	
	// OCULTA LOS ELEMENTOS DE DOCS.
	var hideDocPost = function(){
		logControl ? console.log('hideDocPost()...') : '';

		$("#modalDocs-result-show").hide();
		$("#btn-search-back").hide();
		$("#modalDocs-content").show();				
	}

	// DIALOGO DE CONFIRMACIÓN GENÉRICO
	var showConfirmDialog = function(formId){		
		logControl ? console.log('showConfirmDialog()...') : '';
		
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;		
		
		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: 'CONFIRMACIÓN',
			theme: 'dark',
			content: 'Contenido del dialogo de confirmación genérico',
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
					action: function (){} //GENERIC CLOSE.		
				}
			}
		});		
	}

	// DIALOGO CONFIRMACIÓN ONTOLOGÍA
	var showConfirmDialogOntologia = function(formId){		
		logControl ? console.log('showConfirmDialogOntologia()...') : '';
		
		var Remove = headerReg.btnEliminar;
		var Close = headerReg.btnCancelar;	
		
		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-warning',
			title: 'CONFIRM:',
			theme: 'dark',
			content: 'Contenido del dialogo de confirmación genérico',
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
					action: function (){} //GENERIC CLOSE.		
				}
			}
		});
		
	}
	
	// DIALOGO CONFIRMACIÓN USUARIO
	var showConfirmDialogUsuario = function(formId){		
		logControl ? console.log('showConfirmDialogUsuario()...') : '';
		
		var Close = headerReg.btnCancelar;
		var Remove = headerReg.btnEliminar;
		
		// jquery-confirm DIALOG SYSTEM.
		$.confirm({
			icon: 'fa fa-bug',
			title: 'USER CONFIRM:',
			theme: 'dark',
			content: 'contenido pendiente de colocar.',
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
					btnClass: 'btn-sm btn-danger btn-outline',
					action: function (){} //GENERIC CLOSE.		
				}
			}
		});
	}	
	
	// ERRORES de SERVIDOR
	var errores = function(){		
		var Close = headerReg.btnCancelar;
		if ( headerReg.errores !== null ){			
			// jquery-confirm DIALOG SYSTEM.
			$.confirm({
				icon: 'fa fa-bug',
				title: 'ERROR',
				theme: 'dark',
				content: headerReg.errores,
				draggable: true,
				dragWindowGap: 100,
				backgroundDismiss: true,
				closeIcon: true,
				buttons: {				
					close: {
						text: Close,
						btnClass: 'btn-sm btn-danger btn-outline',
						action: function (){} //GENERIC CLOSE.		
					}
				}
			});			
		} else { logControl ? console.log('errores() -> NO ERROR.') : ''; }		
	}
	
	// INFORMACION DEL SERVIDOR (ERRORES)
	var informacion = function(){		
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
						btnClass: 'btn-sm btn-danger btn-outline',
						action: function (){} //GENERIC CLOSE.		
					}
				}
			});
		}
		else { logControl ? console.log('informacion() -> SIN INFO ERRORES.') : ''; }		
	}	
	
	// ZONA DE FUNCIONES PUBLICAS ENTRE ELLAS INIT.
	return{
		
		// LOAD() CARGA UN JSON DE PARAMETROS DE LA PLANTILLA A LA LIB.
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': Función load()') : '';
			return headerReg = Data;
		},
		
		// INIT() INICIALIZACION DE LA LIB , EJECUCIÓN DE TODAS LAS FUNCIONES QUE SE DEBEN LANZAR AL ENTRAR EN LA PÁGINA.
		init: function(){
			logControl ? console.log(LIB_TITLE + ': Función init()') : '';
			
			// control de errores de servidor.
			errores();			
			// control de informacion cuando hay errores.
			informacion();
		},
		
		// SEARCH
		search: function(){
			logControl ? console.log(LIB_TITLE + ': Función search()') : '';
			searchDocs();			
		},
		
		// CONTROL DE ERRORES LAS PLANTILLAS LLAMAN A ESTAS FUNCIONES CUANDO FALLAN CMAPOS, O VALIDACIONES ETC...
		showErrorDialog: function(){		
			logControl ? console.log('showErrorDialog()...') : '';
			var Close = headerReg.btnCancelar;
		
			$( "#dialog-error" ).dialog({resizable: false, height:160, modal: true, position: [($(window).width() / 2) - 150, 160], dialogClass: 'DeleteConfirmDialog',
				buttons: {
					Close: function() { $( this ).dialog( "close" ); }
				}
			});		
		},
		
		// DIALOGO DE CONFIRMACIÓN GENÉRICO
		showConfirmDialog : function(formId){		
			logControl ? console.log('showConfirmDialog()...') : '';
			showConfirmDialog(formId);
		},

		// DIALOGO CONFIRMACIÓN ONTOLOGÍA
		showConfirmDialogOntologia : function(formId){		
			logControl ? console.log('showConfirmDialogOntologia()...') : '';
			showConfirmDialogOntologia(formId);
		},
	
		// DIALOGO CONFIRMACIÓN USUARIO
		showConfirmDialogUsuario : function(formId){		
			logControl ? console.log('showConfirmDialogUsuario()...') : '';
			showConfirmDialogUsuario();
		}		
	};
}();

// INICIALIZACIÓN AUTOMÁTICA DEL CONTROLADOR
jQuery(document).ready(function() {
	
	// LLAMAMOS AL JSON DE ETIQUETAS DE LENGUAJE
	HeaderController.load(headerJson);
	
	// LLAMAMOS AL INIT
	HeaderController.init();
});
