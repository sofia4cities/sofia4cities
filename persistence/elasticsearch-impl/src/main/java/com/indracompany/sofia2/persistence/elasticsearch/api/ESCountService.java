package com.indracompany.sofia2.persistence.elasticsearch.api;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ESCountService {

	@Autowired
	ESBaseApi connector;

    public long getMatchAllQueryCount(String... indexes) {
        QueryBuilder query = matchAllQuery();
        log.info("getMatchAllQueryCount query =>"+ query.toString());
        long count = connector.getClient().prepareSearch(indexes).setQuery(query).setSize(0).execute().actionGet().getHits().getTotalHits();
        return count;
    }


    public long getQueryCount(String jsonQueryString,String... indexes) {
    	log.info("getQueryCount query =>"+ jsonQueryString.toString());
        WrapperQueryBuilder build = QueryBuilders.wrapperQuery(jsonQueryString);
        long count = connector.getClient().prepareSearch(indexes).setQuery(build).setSize(0).execute().actionGet().getHits().getTotalHits();
        return count;
    }

}