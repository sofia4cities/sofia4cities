var loadJsonFromDoc = function(files){
		var reader = new FileReader();
		var size = files[0].size;
		var chunk_size = Math.pow(2, 13);
	    var chunks = [];
	    var offset = 0;
	    var bytes = 0;
		if(files[0].type == "text/xml"){

			reader.onloadend = function (e) {
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
							myCodeMirror.setValue(JSON.stringify(x2js.xml_str2json(content)));
							myCodeMirrorJsonImport.setValue(JSON.stringify(x2js.xml_str2json(content)));
						
						}
						
						
					}
				
				}	
				var blob = files[0].slice(offset, offset + chunk_size);
				reader.readAsText(blob);
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
						myCodeMirror.setValue(csvJSON(content));
						myCodeMirrorJsonImport.setValue(csvJSON(content));
						
					}
					
				}
				
			}
			var blob = files[0].slice(offset, offset + chunk_size);
			reader.readAsText(blob);
			
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
							myCodeMirror.setValue(content);
							myCodeMirrorJsonImport.setValue(content);
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
							myCodeMirror.setValue(JSON.stringify(arrayJson));
							myCodeMirrorJsonImport.setValue(JSON.stringify(arrayJson));
						}
						
						
						
					}
				}
				
				/*var jsonData = reader.result;
				*/
			}
			var blob = files[0].slice(offset, offset + chunk_size);
			reader.readAsText(blob);
		}
};
		

		
