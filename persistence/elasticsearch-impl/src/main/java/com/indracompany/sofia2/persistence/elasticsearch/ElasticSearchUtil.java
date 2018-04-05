package com.indracompany.sofia2.persistence.elasticsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ElasticSearchUtil {
	
	
	public static String parseElastiSearchResult(String response) throws JSONException {
		
		JSONArray hitsArray = null;
		JSONObject hits = null;
		JSONObject source = null;
		JSONObject json = null;
		
		JSONArray jsonArray = new JSONArray();
		
		json = new JSONObject(response);
		hits = json.getJSONObject("hits");
		hitsArray = hits.getJSONArray("hits");
				
		for (int i=0; i<hitsArray.length(); i++) {
			JSONObject h = hitsArray.getJSONObject(i);
			source = h.getJSONObject("_source");
			jsonArray.put(source);
		}
		
		return jsonArray.toString();
		
	}

}
