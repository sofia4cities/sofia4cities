/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2016 - 2017  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
function addQuotesToData(data){
	if (data.indexOf("{")!=0)
		data="{"+data+"}";
		
	return data;
}

function escapeJSONObject(datos){
	return datos.replace(/\"/g, "\\\"").replace(/\\\\\"/g, "\\\\\\\"");
}

var SSAPResourceGenerator = {

	
	/**
	 * JOIN By Token
	 */
	generateJoinByTokenMessage : function(kp, instance, token) {
		
		var queryJoin = '{"join":true,"instanceKP":"'
			+ instance
			+ '","token":"'
			+ token
			+ '"}';
		
		return queryJoin;
	},
	
	/**
	 * LEAVE Operation
	 */
	generateLeaveMessage : function(sessionKey) {
		var queryLeave = '{"leave":true,"sessionKey":"'
				+ sessionKey
				+ '"}';
		
		return queryLeave;
	},
	
	/**
	 * INSERT Message
	 */
	generateInsertMessage : function(data, ontology, sessionKey) {
		data = escapeJSONObject(data.toString());
		var queryInsert = '{"sessionKey":"'
				+ sessionKey+'","ontology":"'
				+ ontology
				+ '","data":"'
				+ data	
				+ '"}';
				
				
		return queryInsert;
	},

	/**
	 * UPDATE Operation
	 */
	generateUpdateMessage : function(data, ontology, sessionKey) {
		data = escapeJSONObject(data.toString());
		var queryUpdate = '{"sessionKey":"'
				+ sessionKey
				+ '","ontology":"'
				+ ontology
				+ '","data":"'
				+ data
				+ '"}';
		
		return queryUpdate;
	},	
	
	/**
	 * REMOVE Operation
	 */
	generateDeleteMessage : function(data, ontology, sessionKey) {
		data = escapeJSONObject(data.toString());
		var queryRemove = '{"sessionKey":"'
				+ sessionKey
				+ '","ontology":"'
				+ ontology
				+ '","data":"'
				+ data
				+ '"}';
		
		return queryRemove;
	},	
		
}
exports.generateJoinByTokenMessage = SSAPResourceGenerator.generateJoinByTokenMessage;
exports.generateLeaveMessage = SSAPResourceGenerator.generateLeaveMessage;
exports.generateInsertMessage = SSAPResourceGenerator.generateInsertMessage;
exports.generateUpdateMessage = SSAPResourceGenerator.generateUpdateMessage;
exports.generateDeleteMessage = SSAPResourceGenerator.generateDeleteMessage;

