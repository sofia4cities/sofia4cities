var IssueController = function() {
	
	var client = new SofiaClient();
	
	
	var url = "http://localhost:8081/iotbroker/message";
	var ontology = 'Ticket';
	var device = 'Ticketing';
	var token= '3c0b1c2d17bc47b0b24f3698b8bf6ed4';
	var deviceInstance = new Date();
	var config ={};
	var queryAll= 'db.' + ontology + '.find()'
	var queryType= 'NATIVE';
	
	var newIssue = function() {
		
		
		var data ={'Ticket':{}};
		var coordinates = {'coordinates': {'0':0.0,'1':0.0}};
		data['Ticket']['Identification']=$('#issue').val();
		data['Ticket']['Status'] = 'PENDING';
		data['Ticket']['Email']=$('#email').val();
		data['Ticket']['Name']=$('#name').val();
		data['Ticket']['Response_via']=$('#requesttype').val();
		
		coordinates['coordinates']['0'] =parseFloat($('#longitude').val());	
		coordinates['coordinates']['1'] = parseFloat($('#latitude').val());
		
		data['Ticket']['Coordinates']= coordinates;
	
		data['Ticket']['Type']=$('#issuecategory').val();;
		data['Ticket']['Description']=$('#description').val();;
		//data['Ticket']['File']=;
		
		client.insert(ontology,JSON.stringify(data), function(response){
			if( response.body.data.id != null) {
				alert("Issue registered, thank you for your collaboration. Ticket number : " +response.body.data.id);
			}
			
		});
		
		
	};
	
	var queryAllIssues = function(response) {
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
				
				$('#tableAllIssues tbody').append('<tr><td>'+i+'</td><td>'
						+issue._id.$oid+'</td><td>'
						+issue.Ticket.Identification+'</td><td>'
						+issue.Ticket.Type+'</td><td>'
						+issue.Ticket.Name+'</td><td>'
						+issue.Ticket.Email+'</td><td>'+htmlStatus+'</td></tr>')
			}
		}
		$('#issueForm').addClass('hide');
		$('#issueList').removeClass('hide');
		$('#issueSearch').addClass('hide');
		document.querySelector('.scrolltolist').scrollIntoView({ behavior: 'smooth' , block: 'start'});			

		
	};
	
	var queryForIssue = function (response) {
		$('#tableAllIssues tbody').html("");
		var arrayList = response.body.data;
		
		
		
		
	};
	
	return {
		
		init: function(){

			config['url'] = url;
			config['token'] = token;
			config['clientPlatform'] = device;
			config['clientPlatformInstance'] = deviceInstance;
			client.configure(config);
			client.connect();
			
			
			$(".btn-new-issue").click(function () {
				newIssue();				
			});
			
			$('.btn-list').on('click',function(){
				client.query(ontology, queryAll, queryType,  function(response){queryAllIssues(response)});
			});
			
			$('.btn-search').on('click',function(){
				var query = 'db.' + ontology + '.find({})'
				client.query(ontology, queryAll, queryType,  function(response){queryAllIssues(response)});
			});
		
			
		}
	
		
		
	};
	
	
}();

//AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	IssueController.init();
	
});