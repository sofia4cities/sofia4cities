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
				}
				
			}

		}
		var blob = files[0].slice(offset, offset + chunk_size);
		reader.readAsText(blob);
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
		
