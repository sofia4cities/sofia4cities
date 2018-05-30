var AppController = function(){
	var apimanager = 'http://localhost:19100/api-manager/oauth/token';
	var notebookOpsBaseUrl = 'http://localhost:18000/controlpanel/notebook-ops/';
	var authenticated = false;
	var accessToken;
	var paragraphId = "20180530-084740_1247049587";
	var mapJson;
	var beautify = function(){
		editor.setValue(js_beautify(editor.getValue()));
	};
	var executeNotebook = function() {
		var ntId = $('#ntId').val();
		$.ajax({
			'url' : notebookOpsBaseUrl + "/run/notebook/" + ntId,
			'type' : 'POST',
			'dataType' : 'json',
			'headers' : {
				'Authorization' : accessToken
			},
			'success' : function(result) {
				$.ajax({
					'url' : notebookOpsBaseUrl + "/result/notebook/" + ntId + "/paragraph/" + paragraphId,
					'type' : 'GET',
					'dataType' : 'json',
					'headers' : {
						'Authorization' : accessToken
					},
					'success' : function(result) {				
						$("#main-content").show();
						mapJson = JSON.parse(result.body.results.msg[0].data);
						editor.setValue(result.body.results.msg[0].data);
						beautify();
						loadBubbleMap(mapJson);
					},
					'error' : function(req, status, err) {
						console.log('Could not get paragraph info ' + paragraphId,
								req.responseText, status, err);

					}

				});
				
			},
			'error' : function(req, status, err) {
				console.log('Could not execute notebook ' + ntId,
						req.responseText, status, err);

			}

		});
		

	}
	var login = function (){

		var username = $("#userName").val();
		var password = $("#userPassword").val();
		// The auth_token is the base64 encoded string for the API 
		// application.
		var auth_token = 'sofia2_s4c:sofia2_s4c';
		auth_token = window.btoa(auth_token);
		var requestPayload = {
			// Enter your inContact credentials for the 'username' and 
			// 'password' fields.
			'grant_type' : 'password',
			'username' : username,
			'password' : password
		}
		$.ajax({
			'url' : apimanager,
			'type' : 'POST',
			'content-Type' : 'x-www-form-urlencoded',
			'dataType' : 'json',
			'headers' : {
				'Authorization' : 'Basic ' + auth_token
			},
			'data' : requestPayload,
			'success' : function(result) {
				
				accessToken = result.access_token;
				if(accessToken != null) {
					accessToken = "Bearer " + accessToken;
					authenticated = true;
					$('#login').addClass('hide');
					$('#execute-nt').show();
				}
				return result;
			},
			'error' : function(req, status, err) {
				console.log('something went wrong',
						req.responseText, status, err);

			}

		});
		

	};

	return{

		init: function(){
			$("#btn-login").on('click', function(){
				login();
			});
			$("#execute").on('click', function(){
				executeNotebook();
			});
		},
		getToken : function(){
			return accessToken;
		},
		beautify : function(){
			beautify();
		}


	};
}();

//AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	AppController.init();
	
});



