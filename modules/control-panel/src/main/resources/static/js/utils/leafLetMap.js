var mymap;
var markers = new Array();
var filteredDevices = new Array();
var setUpMap = function(id) {
	
	mymap = L.map(id).setView([devices[0].location[0], devices[0].location[1]],6);
	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
	    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
	    maxZoom: 18,
	    id: 'mapbox.streets',
	    accessToken: 'pk.eyJ1IjoiZmpnY29ybmVqbyIsImEiOiJjamgxbm9nOW8wN2EwMnhsbm1nNnNvOXRsIn0.6RzVaJ2kUwaFNLJW4AzRQg'
	}).addTo(mymap);
	
	drawMarkers();
	$('#map-portlet-body').hide();
} 
var updateMarkers = function(){
	removeMarkers();
	filteredDevices = new Array();
	var tagsFilter = $('#tagsFilter input').val();
	var statusFilter = $('#statusFilter input').val();
	if(tagsFilter != "" || statusFilter != ""){
		var elements = $('#devicesTable > tbody > tr');
		if(elements[0].cells.length == 1) filteredDevices.push("noDevicesMatched");
//	for(var i= elements.length-1; i>=0 ; i--){
//			filteredDevices.push(elements[i].cells[0].firstChild.data);
//		}
		for(var i= devices.length-1; i>=0 ; i--){
			if(tagsFilter != "" && statusFilter != ""){
				if(devices[i].tags.toLowerCase().indexOf(tagsFilter.toLowerCase()) > -1 && devices[i].status.toLowerCase().indexOf(statusFilter.toLowerCase()) > -1){
					filteredDevices.push(devices[i].identification);
				}
			}else if(tagsFilter != ""){
				if(devices[i].tags.toLowerCase().indexOf(tagsFilter.toLowerCase()) > -1){
					filteredDevices.push(devices[i].identification);
				}
			}else if(statusFilter != ""){
				if(devices[i].status.toLowerCase().indexOf(statusFilter.toLowerCase()) > -1){
					filteredDevices.push(devices[i].identification);
				}
			}
			
		}
	}
	drawMarkers();	
}
var removeMarkers = function(){
	for(i=0;i<markers.length;i++) {
		mymap.removeLayer(markers[i]);
	}
	markers= new Array();
}
var drawMarkers = function (){
	
	for(var i= devices.length-1; i>=0 ; i--){
		if((filteredDevices.indexOf(devices[i].identification) > -1) || (filteredDevices.length == 0)){
			var status = devices[i].status;
			var iconCustom;
			var connected = devices[i].connected;
			if(status == "WARNING"){
				iconCustom = new L.Icon({
					  iconUrl: '/controlpanel/static/vendor/leaflet/images/marker-icon-yellow.png',
					  shadowUrl: '/controlpanel/static/vendor/leaflet/images/marker-shadow.png',
					  iconSize: [25, 41],
					  iconAnchor: [12, 41],
					  popupAnchor: [1, -34],
					  shadowSize: [41, 41]
					});
			}else if(status == "ERROR"){
				iconCustom = new L.Icon({
					  iconUrl: '/controlpanel/static/vendor/leaflet/images/marker-icon-red.png',
					  shadowUrl: '/controlpanel/static/vendor/leaflet/images/marker-shadow.png',
					  iconSize: [25, 41],
					  iconAnchor: [12, 41],
					  popupAnchor: [1, -34],
					  shadowSize: [41, 41]
					});
				
			}else if(!connected){
				iconCustom = new L.Icon({
					  iconUrl: '/controlpanel/static/vendor/leaflet/images/marker-icon-grey.png',
					  shadowUrl: '/controlpanel/static/vendor/leaflet/images/marker-shadow.png',
					  iconSize: [25, 41],
					  iconAnchor: [12, 41],
					  popupAnchor: [1, -34],
					  shadowSize: [41, 41]
					});
			}else{
				iconCustom = new L.Icon({
					  iconUrl: '/controlpanel/static/vendor/leaflet/images/marker-icon-green.png',
					  shadowUrl: '/controlpanel/static/vendor/leaflet/images/marker-shadow.png',
					  iconSize: [25, 41],
					  iconAnchor: [12, 41],
					  popupAnchor: [1, -34],
					  shadowSize: [41, 41]
					});
				
			}
			if(devices[i].location != null){
				var marker = L.marker([devices[i].location[0], devices[i].location[1]], {'icon' : iconCustom});
				marker.bindPopup("<b><a target='_blank' href='/controlpanel/devices/management/show/"+devices[i].id+"'>"+devices[i].identification+"</a></b><br/>Status: "+devices[i].status+"<br>Connected: "+devices[i].connected+
						"<br>Protocol: "+devices[i].protocol+"<br>Tags: "+devices[i].tags);
				marker.addTo(mymap);
				markers.push(marker);
			}
			
		}
	
	}
	
}