var webProjectCreateController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME = 'Sofia4Cities Control Panel'; 
	var LIB_TITLE = 'Web Project Controller';	
    var logControl = 1;
	var LANGUAGE = ['es'];
	var currentLanguage = ''; // loaded from template.	
	var internalLanguage = 'en';	
	
	// CONTROLLER PRIVATE FUNCTIONS	
	
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		uploadZip: function() {
	    	//mostrarCapaLoading();
	    	$("#pathZipUpload").val($("#identification").val());
			$.ajax({
	            type: 'post',
	            url: '/controlpanel/webprojects/uploadZip',
	            contentType: false,
	            processData: false,
	            data: new FormData($('#uploadZip')[0]),
	            success: function () {
	            	var fancytreeObject = treeObject.fancytree("getTree");
	                fancytreeObject.reload({
	                        contentType: "application/json; charset=utf-8",
	                        url: "/controlpanel/webprojects/getFilesInPath",
	                        data: "path=" + $("#identification").val(),
	                        success: function(data){
	                        	//esconderCapaLoading();
	                            console.log("root zip reload");
	                        },
	                        error: function(){
	                            //esconderCapaLoading();
	                        }
	                })
	            },
	            error: function(){
	                //esconderCapaLoading();
	            }
	        });
	    }
	}
}
