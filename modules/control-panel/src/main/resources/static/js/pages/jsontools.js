var fileLoaded;
var loadJsonFromDoc = function(files){
		var reader = new FileReader();
		var size = files[0].size;
		var chunk_size = Math.pow(2, 13);
	    var chunks = [];
	    var offset = 0;
	    var bytes = 0;
		if(files[0].type == "text/xml"){

				var x2js = new X2JS();
				reader.onloadend = function (e) {
					if(e.target.readyState == FileReader.DONE){
						var chunk = e.target.result;
						bytes += chunk.length;
						
						chunks.push(chunk);
						if(offset < size){
							offset += chunk_size;
							var blob = files[0].slice(offset , offset + chunk_size);
							reader.readAsText(blob);	
						}else{
							var content = chunks.join("");
							fileLoaded=x2js.xml_str2json(content);
							printJson();
						
						}
						
						
					}
					progressBarFileUpload(offset,size);
				}	
				

		}else if (files[0].name.indexOf(".csv")!=-1){
	
			reader.onloadend = function (e) {
				if(e.target.readyState == FileReader.DONE){
					var chunk = e.target.result;
					bytes += chunk.length;
					
					chunks.push(chunk);
					if(offset < size){
						offset += chunk_size;
						var blob = files[0].slice(offset , offset + chunk_size);
						reader.readAsText(blob);	
					}else{
						var content = chunks.join("").replace(/\"/g, '');
						fileLoaded = JSON.parse(csvJSON(content));
						printJson();
					}
					progressBarFileUpload(offset,size);
				}
			
				
			}

		}else if (files[0].type == "application/json"){


		    
			reader.onloadend = function (e) {
				
				if(e.target.readyState == FileReader.DONE){
					var chunk = e.target.result;
					bytes += chunk.length;
					
					chunks.push(chunk);
					
					if(offset < size){
						offset += chunk_size;
						var blob = files[0].slice(offset , offset + chunk_size);
						reader.readAsText(blob);	
					}else{
						
						var content = chunks.join("");
						try{
							var jsonData = JSON.parse(content);
							fileLoaded = jsonData;
						}catch(err){
							var jsonData = content.replace(/[\r]/g, '');
							var arrayJson = [];
							var dataSplitted = jsonData.split("\n");
							var i;
							for(var i in dataSplitted){
								if(dataSplitted[i] != "") {
									arrayJson.push(JSON.parse(dataSplitted[i]));
								}
							}
							fileLoaded=arrayJson;
						
						}
						
						printJson();
						
					}
					progressBarFileUpload(offset,size);
				}
				
			}

		}
		var blob = files[0].slice(offset, offset + chunk_size);
		reader.readAsText(blob);
		$('#progressBarModal').modal("show");
};
		
var printJson = function(){
	
	if(fileLoaded.length > 100){
		myCodeMirror.setValue(JSON.stringify(fileLoaded.slice(0,20)));
		myCodeMirrorJsonImport.setValue(JSON.stringify(fileLoaded.slice(0,20)));
	}else{
		myCodeMirror.setValue(JSON.stringify(fileLoaded));
		myCodeMirrorJsonImport.setValue(JSON.stringify(fileLoaded));
	}
	beautifyJson();
	
};


var importBulkJson = function() {
	if($('#ontology').val() == ""){
		$('#errorSelect').text("Select ontology first");
		$('#ErrorOntSelect').modal("show");
	}else{
		var arrayJson = fileLoaded;
		if(parentNode != null){
			var arrayJson = fileLoaded;
			var newArray = [];
			if(arrayJson.length != null){
				for(var i= arrayJson.length-1; i>=0 ; i--){
					var newObject={};
					newObject[parentNode]=arrayJson[i];
					newArray.push(newObject);					
				}
				
			}else{
				var newObject={};
				newObject[parentNode]=arrayJson;
				newArray=newObject;
			}
			arrayJson = newArray;
				
			
		}
		if(arrayJson.length != null && arrayJson.length > 200){
		
			var counter= 0;
			var infLimit=0;
			var supLimit=200;
			var increment =200;
			$('#importProgress').removeClass('progress-bar-success');
			$('#importProgress').removeClass('progress-bar-danger');
			$('#progressBarModal').modal("show");
			for(var s=arrayJson.length; s>=0; s--) {
				
				if(infLimit > arrayJson.length){
					break;
				}
				if(supLimit > arrayJson.length){
					supLimit = arrayJson.length;
				}
				var subArray = arrayJson.slice(infLimit,supLimit);
				if(subArray.length != null && subArray.length != 0 ){
					jQuery.post('/controlpanel/jsontool/importbulkdata', {'data':JSON.stringify(subArray), 'ontologyIdentification': $('#ontology').val()}, function(data){
						if(data != "Error"){
							counter+=Number(data);
							var percent = (counter/arrayJson.length)*100;
							
							$('#importProgress').attr('aria-valuenow', percent+'%').css('width',percent+'%');
							$('#importProgress').text(percent.toFixed(2)+'%');
							if(counter >= arrayJson.length){
								//$('#response').text(counter + " ontologies inserted of type " + $('#ontology').val());
								//$('#returnAction').modal("show");
								//$('#importProgress').text('Completed');
								$('#importProgress').addClass('progress-bar-success');
								$('#progressResult').text(counter + ' ontologies inserted.');
							}
						}else{
							$('#importProgress').removeClass('active');
							$('#importProgress').addClass('progress-bar-danger');
							$('#importProgress').attr('aria-valuenow', '100%').css('width','100%');
							$('#importProgress').text('Error');
						}
						
					});
				}
				infLimit += increment;
				supLimit += increment;
			}
			
		}else{
			jQuery.post('/controlpanel/jsontool/importbulkdata', {'data':JSON.stringify(arrayJson), 'ontologyIdentification': $('#ontology').val()}, function(data){
				if(data != "Error"){
					if(JSON.parse(myCodeMirrorJsonImport.getValue()).length != null){
						$('#response').text(data + " ontologies inserted of type " + $('#ontology').val());
					}else{
						$('#response').text("Ontology inserted of type " + $('#ontology').val());
					}
					$('#returnAction').modal("show");
				}else{
					$('#response').text(data);
					$('#returnAction').modal("show");
				}
				
			});
			
		}

	}
	
}

var progressBarFileUpload = function(offset, maxSize){
	var percent = (offset/maxSize) *100;
	$('#importProgress').removeClass('progress-bar-success');
	$('#importProgress').removeClass('progress-bar-danger');
	if(offset < maxSize){
		$('#importProgress').attr('aria-valuenow', percent+'%').css('width',percent+'%');
		$('#importProgress').text(percent.toFixed(2)+'%');		
	}else{
		$('#importProgress').attr('aria-valuenow', '100%').css('width','100%');
		$('#importProgress').text('100%');
		$('#importProgress').addClass('progress-bar-success');
	}

}
		
