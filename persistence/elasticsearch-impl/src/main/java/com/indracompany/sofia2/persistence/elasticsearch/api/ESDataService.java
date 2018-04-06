/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2.persistence.elasticsearch.api;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.persistence.elasticsearch.ElasticSearchRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESDataService {


	@Autowired
	ESBaseApi connector;

    public List<String> findMatchAllQueryData(String... indexes) {
        QueryBuilder query = matchAllQuery();
        log.info("getMatchAllQueryCount query =>" + query.toString());
        SearchHit[] hits = connector.getClient().prepareSearch(indexes).setQuery(query).execute().actionGet().getHits().getHits();
        List<String> list = new ArrayList<String>();
        for (SearchHit hit : hits) {
            // hit.sourceAsMap()
            list.add(hit.getSourceAsString());
        }
        return list;
    }


    public List<String> findQueryData(String jsonQueryString, String... indexes) {
        log.info("getPhraseQueryCount query =>" + jsonQueryString.toString());
        WrapperQueryBuilder build = QueryBuilders.wrapperQuery(jsonQueryString);
        SearchHit[] hits = connector.getClient().prepareSearch(indexes).setQuery(build).execute().actionGet().getHits().getHits();
        List<String> list = new ArrayList<String>();
        for (SearchHit hit : hits) {
            // hit.sourceAsMap()
            list.add(hit.getSourceAsString());
        }
        return list;
    }
    
    public GetResponse findByIndex(String index, String type, String id) {
        try {
            GetResponse getResponse = connector.getClient().prepareGet(index, type, id).get();
            return getResponse;
        } catch (Exception e) {
            log.error("findDocumentByIndex", e);
        }
        return null;
    }
    
    public MultiGetResponse findByMultipleIndexes(List<ElasticSearchRequest> requests) {
    	try {
    		MultiGetRequestBuilder builder = connector.getClient().prepareMultiGet();
    		for (ElasticSearchRequest _request : requests) {
                builder.add(_request.getIndex(), _request.getType(), _request.getId());
    		}
    		return builder.get();
        } catch (Exception e) {
        	log.error("findByMultipleIndexes", e);
        }
    	return null;
    }
    
    public SearchResponse findDocument(String index, String type, String field, String value) {
        try {
            QueryBuilder queryBuilder = new MatchQueryBuilder(field, value);
            SearchResponse response = connector.getClient().prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .setQuery(queryBuilder)
                    .setFrom(0).setSize(60).setExplain(true)
                    .execute()
                    .actionGet();
            SearchHit[] results = response.getHits().getHits();
            return response;
        } catch (Exception e) {
        	log.error("findDocument", e);
        }
        return null;
    }
    
    public List<SearchResponse> getAllMultiResponseHits(MultiSearchResponse MultiSearchResponse) {
        try {
            List<SearchResponse> result = new ArrayList<SearchResponse>();
            for (MultiSearchResponse.Item item : MultiSearchResponse.getResponses()) {
                SearchResponse response = item.getResponse();
                result.add(response);
            }
            return result;
        } catch (Exception e) {
        	log.error("getAllMultiResponseHits", e);
        }
        return null;
    }
    
    public SearchHits findInIndex(String index, String key, String value) {
        try {
            SearchResponse response = connector.getClient().prepareSearch(index)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery(key, value)) // Query
                    //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18)) // Filter
                    .setFrom(0).setSize(60).setExplain(true)
                    .execute()
                    .actionGet();
            return response.getHits();
        } catch (Exception e) {
        	log.error("findInIndex", e);
        }
        return null;
    }

    public SearchHits findInCluster(String key, String value) {
        try {
            SearchResponse response = connector.getClient().prepareSearch()
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery(key, value)) // Query
                    .execute()
                    .actionGet();
            return response.getHits();
        } catch (Exception e) {
        	log.error("findInCluster", e);
        }
        return null;
    }

    public SearchHits findByQuery(QueryBuilder builder) {
        try {
            SearchResponse response = connector.getClient().prepareSearch()
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(builder) // Query
                    .execute()
                    .actionGet();
            return response.getHits();
        } catch (Exception exception) {
        	log.error(" findByQuery ", exception);
        }
        return null;
    }

    public MultiSearchResponse multiSearch(List<SearchRequestBuilder> searchRequestList) {
        try {
            MultiSearchRequestBuilder builder = connector.getClient().prepareMultiSearch();
            for (SearchRequestBuilder requestBuilder : searchRequestList) {
                builder.add(requestBuilder);
            }
            return builder.execute().actionGet();
        } catch (Exception e) {

        }
        return null;
    }
    
    protected SearchHit[] queryAllAndSort(String index, String type, QueryBuilder qb,List<SortBuilder> sortBuilderList){
       
        SearchRequestBuilder searchRequestBuilder = connector.getClient().prepareSearch().setIndices(index).setTypes(type).setQuery(qb).setSize(10000);
        if(null!=sortBuilderList){
            for(SortBuilder sortBuilder: sortBuilderList){
                searchRequestBuilder.addSort(sortBuilder);
            }
        }
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHit[] hits = searchResponse.getHits().getHits();
      
        return hits;
    }

    protected Aggregations aggregationQuery(String index, String type, QueryBuilder qb, AggregationBuilder aggregationBuilder){
       
        SearchRequestBuilder searchRequestBuilder = connector.getClient().prepareSearch().setIndices(index).setTypes(type).setQuery(qb).addAggregation(aggregationBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Aggregations aggregations = searchResponse.getAggregations();
       
        return aggregations;
    }


    protected SearchHits pageQueryAndSort(String index, String type, QueryBuilder qb, List<SortBuilder> sortBuilderList, int from , int size){
       
        SearchRequestBuilder searchRequestBuilder = connector.getClient().prepareSearch().setIndices(index).setTypes(type).setQuery(qb).setFrom(from).setSize(size);
        if(null!=sortBuilderList){
            for(SortBuilder sortBuilder: sortBuilderList){
                searchRequestBuilder.addSort(sortBuilder);
            }
        }
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHits hits = searchResponse.getHits();
       
        return hits;
    }
    
   

   

}