var setUpMap = function(id) {
	
	var mymap = L.map(id).setView([devices[0].location[0], devices[0].location[1]],2);
	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
	    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
	    maxZoom: 18,
	    id: 'mapbox.streets',
	    accessToken: 'pk.eyJ1IjoiZmpnY29ybmVqbyIsImEiOiJjamgxbm9nOW8wN2EwMnhsbm1nNnNvOXRsIn0.6RzVaJ2kUwaFNLJW4AzRQg'
	}).addTo(mymap);
} 