var IssueController = function() {
	
	var client = new SofiaClient();
	
	//var apimanager = 'https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/oauth/token';
	//var iotbroker = "https://s4citiespro.westeurope.cloudapp.azure.com/iotbroker/message";
	var apimanager = 'https://rancher.sofia4cities.com/api-manager/oauth/token';
	var iotbroker = "https://rancher.sofia4cities.com/iotbroker/message";
	//var apimanager = 'https://rancher.sofia4cities.com/api-manager/oauth/token';
	//var iotbroker = "https://rancher.sofia4cities.com/iotbroker/message";
	var ontology = 'Ticket';
	var deviceTemplate = 'Ticketing App';
	var token= '1c7954dd4c7c47e0916e8ea64e3c9967';

	var device= 'Web';
	var config ={};
	var queryAll= 'db.' + ontology + '.find()'
	var queryType= 'NATIVE';
	var isAuthenticated = false;
	var states = ['PENDING','DONE','WORKING', 'STOPPED'];
	var filesToUpload=[];

	var comboSelect;

	var edit = function(id) {
		var queryUpdate = 'db.' + ontology + '.update({\'_id\':{\'$oid\':\''+id+'\'}},{\'Ticket.Status\':\''+$('#'+id +' select').val()+ '\'}';
		client.update(ontology,queryUpdate,function(response) {
			
			$('.btn-list').trigger('click');

		});
	}
	var newIssue = function() {
	
		
		
		var data ={'Ticket':{}};
		var coordinates = {'coordinates': {}, 'type' : 'Point'};
		var media ={'data': '', 'media':{}};
		data['Ticket']['Identification']=$('#issue').val();
		data['Ticket']['Status'] = 'PENDING';
		data['Ticket']['Email']=$('#email').val();
		data['Ticket']['Name']=$('#name').val();
		data['Ticket']['Response_via']=$('#requesttype').val();
		
		coordinates['coordinates']['0']=parseFloat($('#longitude').val());	
		coordinates['coordinates']['1']= parseFloat($('#latitude').val());
		
		data['Ticket']['Coordinates']= coordinates;
	
		data['Ticket']['Type']=$('#issuecategory').val();;
		data['Ticket']['Description']=$('#description').val();;
		
		if($("#b64").val() != "") {
			for (var i = filesToUpload.length - 1; i >= 0; i--) {
				media['data']= media['data'] + filesToUpload[i];
				if(i!=0) {
					media['data'] = media['data'] +";";
				}
			}
			media['media']['name'] = document.getElementById('file').files[0].name;
			media['media']['storageArea'] = 'SERIALIZED'
			media['media']['binaryEncoding'] = 'Base64';
			media['media']['mime'] = 'image/png';
			data['Ticket']['File'] = media;
		}
		
		
		
		client.insert(ontology,JSON.stringify(data), function(response){
			if( response.body.data.id != null) {
				alert("Issue registered, thank you for your collaboration. Ticket number : " +response.body.data.id);
			}
			
		});
		
		
	};
	
	var queryFor = function(response) {
		$('#tableAllIssues tbody').html("");
		var arrayList = response.body.data;
		if(arrayList.length > 0){
			
			for(i = 0; i < arrayList.length; i++) {
				var issue = arrayList[i];
				var status = issue.Ticket.Status;
				var htmlStatus;
				if(status == 'PENDING'){
					htmlStatus = '<span class="label label-sm label-warning">PENDING</span>';
				}else if(status == 'DONE') {
					htmlStatus = '<span class="label label-sm label-success">DONE</span>';
				}else if(status == 'WORKING') {
					htmlStatus = '<span class="label label-sm label-info">WORKING</span>';
				}else if(status == 'STOPPED') {
					htmlStatus = '<span class="label label-sm label-danger">STOPPED</span>';
				}else {
					htmlStatus = status;
				}
				if(issue.Ticket.File != null) {
					var allFiles = issue.Ticket.File.data.split(";");
					var htmlImage="";
					for (var j = allFiles.length - 1; j >= 0; j--) {
						
						htmlImage = htmlImage + '<div class="jpreview-image img-responsive thumbnail" style="background-image: url(data:'
						+ issue.Ticket.File.media.mime + ';' + issue.Ticket.File.media.binaryEncoding + ',' + allFiles[j] + ')" ></div>'
					}
					//var htmlImage = '<div class="jpreview-image img-responsive thumbnail" style="background-image: url(data:'
					//	+ issue.Ticket.File.media.mime + ';' + issue.Ticket.File.media.binaryEncoding + ',' + issue.Ticket.File.data + ')" ></div>'
					

					$('#tableAllIssues tbody').append('<tr id="'+issue._id.$oid+'"><td>'+i+'</td><td>'
							+issue._id.$oid+'</td><td>'
							+issue.Ticket.Identification+'</td><td>'
							+issue.Ticket.Type+'</td><td>'
							+issue.Ticket.Name+'</td><td>'
							+issue.Ticket.Email+'</td><td>'+htmlStatus+comboSelect+'</td><td>'
							+htmlImage+'</td><td><button class="btn btn-white btn-outline" onclick="IssueController.edit(\''+issue._id.$oid+'\')">Edit</button></td></tr>')
					
				}else {
					$('#tableAllIssues tbody').append('<tr id="'+issue._id.$oid+'"><td>'+i+'</td><td>'
							+issue._id.$oid+'</td><td>'
							+issue.Ticket.Identification+'</td><td>'
							+issue.Ticket.Type+'</td><td>'
							+issue.Ticket.Name+'</td><td>'
						+issue.Ticket.Email+'</td><td>'+htmlStatus+comboSelect+'</td><td></td><td><button class="btn btn-white btn-outline" onclick="IssueController.edit(\''+issue._id.$oid+'\')">Edit</button></td></tr>')
					
				}
				
			}
		}
		$('#issueForm').addClass('hide');
		$('#issueList').removeClass('hide');
		$('#issueSearch').addClass('hide');
		document.querySelector('.scrolltolist').scrollIntoView({ behavior: 'smooth' , block: 'start'});			

		
	};
	
	var readFile = function() {
		  for (var i = 0; i < this.files.length; i++) { //for multiple files          
		    (function(file) {
		        var name = file.name;
		        var reader = new FileReader();  
		        reader.onload = function(e) {  
		           	var base64 = e.target.result.split(",")[1];
				    $("#b64").val(base64);
				    filesToUpload.push(base64);
			
		        }
		        console.log(file.name +" loaded");
		        reader.readAsDataURL( file );
		    })(this.files[i]);
		}
		 
		}
	
	var queryForIssue = function (response) {
		$('#tableAllIssues tbody').html("");
		var arrayList = response.body.data;
		
		if(arrayList.length > 0){
			
			
			
		}
		
		$('#issueForm').addClass('hide');
		$('#issueList').removeClass('hide');
		$('#issueSearch').addClass('hide');
		document.querySelector('.scrolltolist').scrollIntoView({ behavior: 'smooth' , block: 'start'});		
		
		
		
	};
	
	return {
		
		edit : function(id) {
			edit(id);
		},
		init: function(){

			config['url'] = iotbroker;
			config['token'] = token;
			config['deviceTemplate'] = deviceTemplate;
			config['device'] = device;
			client.configure(config);
			client.connect();
			
			document.getElementById("file").addEventListener("change", readFile);

			comboSelect = '<select>';
			for (i= 0; i< states.length ; i++ ){
				comboSelect = comboSelect+'<option>'+states[i]+'</option>';
			}
			comboSelect = comboSelect +'</select>';

			$(".btn-new-issue").click(function () {
				newIssue();				
			});
			
			$('.btn-list').on('click',function(){
				if(isAuthenticated == true){
					client.query(ontology, queryAll, queryType,  function(response){queryFor(response)});
					document.querySelector('.scrolltosearch').scrollIntoView({ behavior: 'smooth' , block: 'start'});
				}else {
					$('#issueForm').addClass('hide');
					$('#issueList').addClass('hide');
					$('#issueSearch').addClass('hide');
					$('#issueLogin').removeClass('hide');
					document.querySelector('.scrolltosearch').scrollIntoView({ behavior: 'smooth' , block: 'start'});	
				}
			});
			
			$('.btn-search').on('click',function(){
				var query = 'db.' + ontology + '.find({\'_id\':{\'$oid\':\''+$('#issuesearch').val()+'\'}})'
				client.query(ontology, query, queryType,  function(response){queryFor(response)});
			});
		
			$(".btn-login").on('click',
					function() {
						var username = $("#username").val();
						var password = $("#password").val();

						

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
									isAuthenticated = true;
									$('#badCredentials').addClass('hide');
									$('#issueLogin').addClass('hide');
									$('.btn-list').trigger('click');
								}else {
									$('#badCredentials').show();
								}
								return result;
							},
							'error' : function(req, status, err) {
								console.log('something went wrong',
										req.responseText, status, err);
								$('#badCredentials').removeClass('hide');
							}

						});
					});

			$('.btn-issue-search').on('click',function(){
				if(isAuthenticated == false){
					$('#issueForm').addClass('hide');
					$('#issueList').addClass('hide');
					$('#issueSearch').addClass('hide');
					$('#issueLogin').removeClass('hide');
					document.querySelector('.scrolltosearch').scrollIntoView({ behavior: 'smooth' , block: 'start'});	
					
				}else {
					$('#issueForm').addClass('hide');
					$('#issueList').addClass('hide');
					$('#issueLogin').addClass('hide');
					$('#issueSearch').removeClass('hide');
					document.querySelector('.scrolltosearch').scrollIntoView({ behavior: 'smooth' , block: 'start'});	
				}
						
			});
			
			
		}
	
		
		
	};
	
	
}();

//AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	IssueController.init();
	
});